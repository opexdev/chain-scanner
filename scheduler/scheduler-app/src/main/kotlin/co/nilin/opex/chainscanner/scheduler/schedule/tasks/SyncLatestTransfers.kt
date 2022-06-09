package co.nilin.opex.chainscanner.scheduler.schedule.tasks

import co.nilin.opex.chainscanner.scheduler.api.BlockRangeCalculator
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRecord
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainScannerHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncRecordHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScheduleTask
import co.nilin.opex.chainscanner.scheduler.service.GetTransfersSubTask
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class SyncLatestTransfers(
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val blockRangeCalculator: BlockRangeCalculator,
    private val getTransfersSubTask: GetTransfersSubTask
) : ScheduleTask {
    private val logger: Logger by LoggerDelegate()

    override suspend fun execute(sch: ChainSyncSchedule) {
        val chainScanner = chainScannerHandler.getScannersByName(sch.chainName).firstOrNull()
            ?: throw IllegalStateException("No chain scanner found for chain: ${sch.chainName}")
        val blockRange = blockRangeCalculator.calculateBlockRange(chainScanner, sch.confirmations)
        logger.debug("Fetch transfers on block range: ${blockRange.first} - ${blockRange.last}")
        runCatching {
            coroutineScope {
                val br = blockRange.take(chainScanner.maxBlockRange)
                br.forEach { bn ->
                    launch {
                        getTransfersSubTask.fetch(sch, chainScanner, bn.toBigInteger())
                        updateChainSyncRecord(sch.chainName, bn.toBigInteger())
                    }
                }
            }
        }.onSuccess {
            logger.info("Successfully fetched transfers for block range: ${blockRange.first} - ${blockRange.last}")
        }
    }

    private suspend fun updateChainSyncRecord(chainName: String, blockNumber: BigInteger) {
        val chainSyncRecord = chainSyncRecordHandler.lastSyncRecord(chainName)
            ?: ChainSyncRecord(chainName, LocalDateTime.now(), blockNumber)
        chainSyncRecordHandler.saveSyncRecord(
            chainSyncRecord.copy(syncTime = LocalDateTime.now(), blockNumber = blockNumber)
        )
    }
}
