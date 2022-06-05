package co.nilin.opex.chainscanner.scheduler.schedule.tasks

import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainScannerHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncRetryHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScheduleTask
import co.nilin.opex.chainscanner.scheduler.service.FetchFunction
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
    private val chainScannerHandler: ChainScannerHandler,
    chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val fetchFunction: FetchFunction
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
                        fetchFunction.fetch(sch, chainScanner, chainSyncRetry.blockNumber).onSuccess {
                            val retries = chainSyncRetry.retries + 1
                            chainSyncRetryHandler.save(chainSyncRetry.copy(retries = retries, synced = true))
                        }.getOrThrow()
                    }
                }
            }
        }.onFailure { e ->
            rethrowScheduleExceptions(e, sch, chainScanner)
        }
    }

    private val WebClientResponseException.isTooManyRequests: Boolean
        get() = statusCode == HttpStatus.TOO_MANY_REQUESTS

    private val WebClientRequestException.isConnectionError: Boolean
        get() = mostSpecificCause is ConnectException
}
