package co.nilin.opex.chainscanner.scheduler.schedule.tasks

import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRetry
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.*
import co.nilin.opex.chainscanner.scheduler.exceptions.RateLimitException
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class RetryFailedSyncs(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller,
) : ScheduleTask {
    private val logger: Logger by LoggerDelegate()

    override suspend fun execute(sch: ChainSyncSchedule) {
        val chainScanner = chainScannerHandler.getScannersByName(sch.chainName).firstOrNull() ?: return
        val chainSyncRetries = chainSyncRetryHandler.findAllActive(sch.chainName)
        val blockRange = chainSyncRetries.take(chainScanner.maxBlockRange)
        runCatching {
            coroutineScope {
                blockRange.forEach { chainSyncRetry ->
                    launch {
                        logger.debug("Retry block sync on blockNumber: ${chainSyncRetry.blockNumber}")
                        fetch(sch, chainScanner, chainSyncRetry)
                    }
                }
            }
        }.onFailure { e ->
            when (e) {
                is RateLimitException -> sch.enqueueNextSchedule(chainScanner.delayOnRateLimit.toLong())
                else -> sch.enqueueNextSchedule(sch.errorDelay)
            }
        }
    }

    private suspend fun fetch(
        sch: ChainSyncSchedule,
        chainScanner: ChainScanner,
        chainSyncRetry: ChainSyncRetry
    ) {
        runCatching {
            val response = scannerProxy.getTransfers(chainScanner.url, chainSyncRetry.blockNumber)
            webhookCaller.callWebhook(chainScanner.chainName, response)
            scannerProxy.clearCache(chainScanner.url, chainSyncRetry.blockNumber)
        }.onFailure { e ->
            val retries = chainSyncRetry.retries + 1
            chainSyncRetryHandler.save(
                chainSyncRetry.copy(retries = retries, giveUp = retries >= sch.maxRetries, error = e.message)
            )
            if (e is WebClientResponseException && e.statusCode == HttpStatus.TOO_MANY_REQUESTS)
                throw RateLimitException()
        }.onSuccess {
            val retries = chainSyncRetry.retries + 1
            chainSyncRetryHandler.save(chainSyncRetry.copy(retries = retries, synced = true))
        }
    }

    private suspend fun ChainSyncSchedule.enqueueNextSchedule(nextTimeDiff: Long) {
        val retryTime = LocalDateTime.now().plus(nextTimeDiff, ChronoUnit.SECONDS)
        chainSyncSchedulerHandler.save(copy(executeTime = retryTime))
    }
}
