package co.nilin.opex.chainscan.scheduler.jobs

import co.nilin.opex.chainscan.scheduler.core.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscan.scheduler.core.spi.*
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class RetryFailedSyncsScheduledJob(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller,
    @Value("\${app.on-sync-webhook-url}") private val onSyncWebhookUrl: String
) : ScheduledJob {
    private val logger: Logger by LoggerDelegate()

    override suspend fun execute(sch: ChainSyncSchedule) {
        val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
        val chainSyncRetries = chainSyncRetryHandler.findAllActive(sch.chainName)
        supervisorScope {
            val blockRange = chainSyncRetries.chunked(chain.maxBlockRange).firstOrNull()
            blockRange?.forEach { retry ->
                launch {
                    logger.trace("Retry block sync on blockNumber: ${retry.blockNumber}")
                    runCatching {
                        val response = scannerProxy.getTransfers(chain.url, retry.blockNumber)
                        webhookCaller.callWebhook("$onSyncWebhookUrl/${chain.chainName}", response)
                    }.onFailure { e ->
                        val retries = retry.retries + 1
                        chainSyncRetryHandler.save(retry.copy(retries = retries, giveUp = retries >= sch.maxRetries))
                        when (e) {
                            is WebClientResponseException -> e.takeIf { it.statusCode == HttpStatus.TOO_MANY_REQUESTS }
                                ?.run { sch.nextSchedule(chain.delayOnRateLimit.toLong()) }
                            is Exception -> sch.nextSchedule(sch.errorDelay)
                            else -> sch.nextSchedule(sch.delay)
                        }
                    }.onSuccess {
                        val lastSyncRecord = chainSyncRecordHandler.lastSyncRecord(sch.chainName) ?: ChainSyncRecord(
                            sch.chainName,
                            LocalDateTime.now(),
                            retry.blockNumber
                        )
                        chainSyncRecordHandler.saveSyncRecord(
                            lastSyncRecord.copy(
                                syncTime = LocalDateTime.now(),
                                blockNumber = retry.blockNumber
                            )
                        )
                        val retries = retry.retries + 1
                        chainSyncRetryHandler.save(retry.copy(retries = retries, synced = true))
                    }
                }
            }
        }
    }

    private suspend fun ChainSyncSchedule.nextSchedule(nextTimeDiff: Long) {
        val retryTime = LocalDateTime.now().plus(nextTimeDiff, ChronoUnit.SECONDS)
        chainSyncSchedulerHandler.save(copy(retryTime = retryTime))
    }
}
