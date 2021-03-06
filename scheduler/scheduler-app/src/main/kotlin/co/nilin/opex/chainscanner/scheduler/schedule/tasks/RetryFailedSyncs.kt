package co.nilin.opex.chainscanner.scheduler.schedule.tasks

import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainScannerHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncRetryHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScheduleTask
import co.nilin.opex.chainscanner.scheduler.exceptions.ScannerConnectException
import co.nilin.opex.chainscanner.scheduler.service.GetTransfersSubTask
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class RetryFailedSyncs(
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val getTransfersSubTask: GetTransfersSubTask
) : ScheduleTask {
    private val logger: Logger by LoggerDelegate()

    override suspend fun execute(sch: ChainSyncSchedule) {
        val chainScanner = chainScannerHandler.getScannersByName(sch.chainName).firstOrNull()
            ?: throw IllegalStateException("No chain scanner found for chain: ${sch.chainName}")
        val chainSyncRetries = chainSyncRetryHandler.findAllActive(sch.chainName)
        val blockRange = chainSyncRetries.take(chainScanner.maxBlockRange)
        supervisorScope {
            blockRange.forEach { chainSyncRetry ->
                launch {
                    logger.debug("Retry syncing for chain: ${sch.chainName} block: ${chainSyncRetry.blockNumber}")
                    getTransfersSubTask.fetch(sch, chainScanner, chainSyncRetry.blockNumber).onSuccess {
                        chainSyncRetryHandler.markAsSynced(chainSyncRetry)
                        logger.info("Successfully retried syncing for chain: ${sch.chainName} block: ${chainSyncRetry.blockNumber}")
                    }.onFailure {
                        logger.error("Failed to sync chain: ${sch.chainName} block: ${chainSyncRetry.blockNumber} tries: ${chainSyncRetry.retries}/${chainSyncRetry.maxRetries}")
                        if (it is ScannerConnectException) cancel(it.message ?: "Unknown")
                    }
                }
            }
        }
    }
}
