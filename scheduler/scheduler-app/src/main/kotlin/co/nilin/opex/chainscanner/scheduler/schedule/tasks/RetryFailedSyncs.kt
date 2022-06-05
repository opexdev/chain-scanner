package co.nilin.opex.chainscanner.scheduler.schedule.tasks

import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRetry
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.*
import co.nilin.opex.chainscanner.scheduler.exceptions.RateLimitException
import co.nilin.opex.chainscanner.scheduler.exceptions.ScannerConnectException
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.net.ConnectException

@Service
class RetryFailedSyncs(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller,
) : ScheduleTask, SyncScheduleTaskBase(chainSyncSchedulerHandler) {
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
            rethrowScheduleExceptions(e, sch, chainScanner)
        }
    }

    private suspend fun fetch(
        sch: ChainSyncSchedule,
        chainScanner: ChainScanner,
        chainSyncRetry: ChainSyncRetry
    ) {
        runCatching {
            scannerProxy.getTransfers(chainScanner.url, chainSyncRetry.blockNumber)
        }.onFailure {
            chainSyncRetryHandler.increaseRetryCounter(chainSyncRetry, sch, it.message)
            if (it is WebClientResponseException && it.isTooManyRequests) throw RateLimitException()
            else if (it is WebClientRequestException && it.isConnectionError) throw ScannerConnectException("Get transfers")
        }.mapCatching {
            webhookCaller.callWebhook(chainScanner.chainName, it)
        }.onFailure {
            chainSyncRetryHandler.increaseRetryCounter(chainSyncRetry, sch, it.message)
        }.mapCatching {
            scannerProxy.clearCache(chainScanner.url, chainSyncRetry.blockNumber)
        }.onFailure { e ->
            if (e is WebClientRequestException && e.isConnectionError) throw ScannerConnectException("Get transfers")
        }.onSuccess {
            val retries = chainSyncRetry.retries + 1
            chainSyncRetryHandler.save(chainSyncRetry.copy(retries = retries, synced = true))
        }
    }

    private val WebClientResponseException.isTooManyRequests: Boolean
        get() = statusCode == HttpStatus.TOO_MANY_REQUESTS

    private val WebClientRequestException.isConnectionError: Boolean
        get() = mostSpecificCause is ConnectException
}
