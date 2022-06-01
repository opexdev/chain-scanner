package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.*
import co.nilin.opex.chainscan.scheduler.coroutines.Dispatchers
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRetry
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class ScheduleService(
    private val scannerProxy: ScannerProxy,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller,
    private val chainScannerHandler: ChainScannerHandler,
    @Value("\${app.on-sync-webhook-url}") private val onSyncWebhookUrl: String,
) {
    private val logger: Logger by LoggerDelegate()
    private val mainSyncScope = CoroutineScope(Dispatchers.SCHEDULER)
    private val retrySyncScope = CoroutineScope(Dispatchers.SCHEDULER)

    @Scheduled(fixedDelay = 1000)
    fun start() {
        if (mainSyncScope.isCompleted()) {
            mainSyncScope.launch {
                supervisorScope {
                    logger.info("Running start() schedule")
                    val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
                    schedules.forEach { sch ->
                        launch {
                            withTimeoutOrNull(sch.timeout) {
                                val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
                                val currentBlockNumber = scannerProxy.getBlockNumber(chain.url)
                                val head = currentBlockNumber - chain.confirmations.toBigInteger()
                                val startBlockNumber =
                                    chainSyncRecordHandler.lastSyncedBlockedNumber(sch.chainName)?.plus(BigInteger.ONE)
                                        ?: head
                                val endBlockNumber = head.max(startBlockNumber)
                                val blockRange = startBlockNumber.toLong()..endBlockNumber.toLong()
                                runCatching {
                                    coroutineScope {
                                        blockRange.chunked(chain.maxBlockRange).forEach { br ->
                                            launch {
                                                br.forEach { bn ->
                                                    launch {
                                                        runCatching {
                                                            val response =
                                                                scannerProxy.getTransfers(chain.url, bn.toBigInteger())
                                                            webhookCaller.callWebhook(
                                                                onSyncWebhookUrl,
                                                                response.transfers
                                                            )
                                                            val record =
                                                                chainSyncRecordHandler.lastSyncRecord(sch.chainName)
                                                            chainSyncRecordHandler.saveSyncRecord(
                                                                record?.copy(
                                                                    syncTime = LocalDateTime.now(),
                                                                    blockNumber = response.blockNumber
                                                                )
                                                                    ?: ChainSyncRecord(
                                                                        sch.chainName,
                                                                        LocalDateTime.now(),
                                                                        response.blockNumber
                                                                    )
                                                            )
                                                        }.onFailure {
                                                            chainSyncRetryHandler.save(
                                                                ChainSyncRetry(
                                                                    sch.chainName,
                                                                    bn.toBigInteger()
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    chainSyncSchedulerHandler.save(
                                        sch.copy(
                                            retryTime = LocalDateTime.now().plus(
                                                sch.errorDelay,
                                                ChronoUnit.SECONDS
                                            )
                                        )
                                    )
                                }.onFailure { e ->
                                    when (e) {
                                        is WebClientResponseException -> e.takeIf { it.rawStatusCode == 429 }
                                            ?.apply {
                                                chainSyncSchedulerHandler.save(
                                                    sch.copy(
                                                        retryTime = LocalDateTime.now().plus(
                                                            chain.rateLimitDelay.toLong(),
                                                            ChronoUnit.SECONDS
                                                        )
                                                    )
                                                )
                                            }
                                        is Exception -> chainSyncSchedulerHandler.save(
                                            sch.copy(
                                                retryTime = LocalDateTime.now().plus(
                                                    sch.errorDelay,
                                                    ChronoUnit.SECONDS
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    fun startRetry() {
        if (retrySyncScope.isCompleted()) {
            retrySyncScope.launch {
                supervisorScope {
                    logger.info("Running startRetry() schedule")
                    val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
                    schedules.forEach { sch ->
                        launch {
                            withTimeoutOrNull(sch.timeout) {
                                val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
                                val chainSyncRetries = chainSyncRetryHandler.findAllActive(sch.chainName)
                                supervisorScope {
                                    chainSyncRetries.forEach { retry ->
                                        launch {
                                            runCatching {
                                                val response = scannerProxy.getTransfers(chain.url, retry.blockNumber)
                                                webhookCaller.callWebhook(onSyncWebhookUrl, response.transfers)
                                                chainSyncRecordHandler.lastSyncRecord(sch.chainName)
                                            }.onFailure {
                                                val retries = retry.retries + 1
                                                chainSyncRetryHandler.save(
                                                    retry.copy(
                                                        retries = retries,
                                                        giveUp = retries >= 5
                                                    )
                                                )
                                            }.onSuccess {
                                                val retries = retry.retries + 1
                                                chainSyncRetryHandler.save(retry.copy(retries = retries, synced = true))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun CoroutineScope.isCompleted() = coroutineContext.job.children.all { it.isCompleted }
}
