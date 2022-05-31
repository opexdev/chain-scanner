package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.*
import co.nilin.opex.chainscan.scheduler.coroutines.Dispatchers
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRetry
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

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
                logger.info("Running start() schedule")
                val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
                schedules.forEach { sch ->
                    launch {
                        val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
                        val currentBlockNumber = scannerProxy.getBlockNumber(chain.url)
                        val startBlockNumber =
                            chainSyncRecordHandler.lastSyncedBlockedNumber(sch.chainName) ?: currentBlockNumber
                        val endBlockNumber = currentBlockNumber - chain.confirmations.toBigInteger()
                        val blockRange = startBlockNumber.toLong()..endBlockNumber.toLong()
                        blockRange.forEach {
                            launch {
                                runCatching {
                                    val response = scannerProxy.getTransfers(chain.url, it.toBigInteger())
                                    webhookCaller.callWebhook(onSyncWebhookUrl, response.transfers)
                                    val record = chainSyncRecordHandler.lastSyncRecord(sch.chainName)
                                    chainSyncRecordHandler.saveSyncRecord(
                                        record?.copy(syncTime = LocalDateTime.now(), blockNumber = response.blockNumber)
                                            ?: ChainSyncRecord(sch.chainName, LocalDateTime.now(), response.blockNumber)
                                    )
                                }.onFailure {
                                    chainSyncRetryHandler.save(ChainSyncRetry(sch.chainName, BigInteger.ZERO))
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
                logger.info("Running startRetry() schedule")
                val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
                schedules.forEach { sch ->
                    launch {
                        val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
                        val chainSyncRetries = chainSyncRetryHandler.findAllActive(sch.chainName)
                        chainSyncRetries.forEach { retry ->
                            launch {
                                runCatching {
                                    val response = scannerProxy.getTransfers(chain.url, retry.blockNumber)
                                    webhookCaller.callWebhook(onSyncWebhookUrl, response.transfers)
                                    chainSyncRecordHandler.lastSyncRecord(sch.chainName)
                                }.onFailure {
                                    val retries = retry.retries + 1
                                    chainSyncRetryHandler.save(retry.copy(retries = retries, giveUp = retries >= 5))
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

    private fun CoroutineScope.isCompleted() = coroutineContext.job.children.all { it.isCompleted }
}
