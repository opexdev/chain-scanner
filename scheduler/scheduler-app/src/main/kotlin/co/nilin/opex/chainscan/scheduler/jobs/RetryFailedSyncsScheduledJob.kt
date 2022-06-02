package co.nilin.opex.chainscan.scheduler.jobs

import co.nilin.opex.chainscan.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscan.scheduler.core.po.ChainSyncRetry
import co.nilin.opex.chainscan.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscan.scheduler.core.spi.*
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
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
) : ScheduledJob {
    private val logger: Logger by LoggerDelegate()

    override suspend fun execute(sch: ChainSyncSchedule) {
        val chainScanner = chainScannerHandler.getScannersByName(sch.chainName).first()
        val chainSyncRetries = chainSyncRetryHandler.findAllActive(sch.chainName)
        runCatching {
            coroutineScope {
                val blockRange = chainSyncRetries.chunked(chainScanner.maxBlockRange).firstOrNull()
                blockRange?.forEach { retry ->
                    launch {
                        logger.trace("Retry block sync on blockNumber: ${retry.blockNumber}")
                        fetch(chainScanner, retry, sch)
                    }
                }
            }
        }.onFailure { e ->
            when (e) {
                is WebClientResponseException -> e.takeIf { it.statusCode == HttpStatus.TOO_MANY_REQUESTS }
                    ?.run { sch.enqueueNextSchedule(chainScanner.delayOnRateLimit.toLong()) }
                    ?: sch.enqueueNextSchedule(sch.errorDelay)
                else -> sch.enqueueNextSchedule(sch.errorDelay)
            }
        }
    }

    private suspend fun fetch(
        chain: ChainScanner,
        retry: ChainSyncRetry,
        sch: ChainSyncSchedule
    ) {
        runCatching {
            val response = scannerProxy.getTransfers(chain.url, retry.blockNumber)
            webhookCaller.callWebhook(chain.chainName, response)
        }.onFailure { e ->
            val retries = retry.retries + 1
            chainSyncRetryHandler.save(
                retry.copy(
                    retries = retries,
                    giveUp = retries >= sch.maxRetries,
                    error = e.message
                )
            )
            when (e) {
                is WebClientResponseException -> e.takeUnless { it.statusCode == HttpStatus.TOO_MANY_REQUESTS }
                    ?: throw e
            }
        }.onSuccess {
            val retries = retry.retries + 1
            chainSyncRetryHandler.save(retry.copy(retries = retries, synced = true))
        }
    }

    private suspend fun ChainSyncSchedule.enqueueNextSchedule(nextTimeDiff: Long) {
        val retryTime = LocalDateTime.now().plus(nextTimeDiff, ChronoUnit.SECONDS)
        chainSyncSchedulerHandler.save(copy(retryTime = retryTime))
    }
}
