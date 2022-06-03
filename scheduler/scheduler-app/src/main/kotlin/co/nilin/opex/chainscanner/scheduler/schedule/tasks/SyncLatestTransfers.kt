package co.nilin.opex.chainscanner.scheduler.schedule.tasks

import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRecord
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
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class SyncLatestTransfers(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller
) : ScheduleTask {
    private val logger: Logger by LoggerDelegate()

    override suspend fun execute(sch: ChainSyncSchedule) {
        val chainScanner = chainScannerHandler.getScannersByName(sch.chainName).firstOrNull() ?: return
        val blockRange = calculateBlockRange(chainScanner, sch.confirmations)
        logger.debug("Fetch transfers on block range: ${blockRange.first} - ${blockRange.last}")
        runCatching {
            coroutineScope {
                val br = blockRange.take(chainScanner.maxBlockRange)
                br.forEach { bn ->
                    launch { fetch(sch, chainScanner, bn.toBigInteger()) }
                }
            }
        }.onFailure { e ->
            when (e) {
                is RateLimitException -> sch.enqueueNextSchedule(chainScanner.delayOnRateLimit.toLong())
                else -> sch.enqueueNextSchedule(sch.errorDelay)
            }
        }.onSuccess {
            sch.enqueueNextSchedule(sch.delay)
        }
    }

    private suspend fun calculateBlockRange(chainScanner: ChainScanner, confirmations: Int): LongRange {
        val chainHeadBlock = scannerProxy.getBlockNumber(chainScanner.url)
        val confirmedBlock = chainHeadBlock - confirmations.toBigInteger()
        val lastSyncedBlock = chainSyncRecordHandler.lastSyncedBlockedNumber(chainScanner.chainName)
        val startBlock = lastSyncedBlock?.plus(BigInteger.ONE) ?: confirmedBlock
        val endBlock = confirmedBlock.min(startBlock + chainScanner.maxBlockRange.toBigInteger())
        return startBlock.toLong()..endBlock.toLong()
    }

    private suspend fun ChainSyncSchedule.enqueueNextSchedule(nextTimeDiff: Long) {
        val retryTime = LocalDateTime.now().plus(nextTimeDiff, ChronoUnit.SECONDS)
        chainSyncSchedulerHandler.save(copy(executeTime = retryTime))
    }

    private suspend fun fetch(sch: ChainSyncSchedule, chainScanner: ChainScanner, blockNumber: BigInteger) {
        runCatching {
            val response = scannerProxy.getTransfers(chainScanner.url, blockNumber)
            webhookCaller.callWebhook(chainScanner.chainName, response)
            scannerProxy.clearCache(chainScanner.url, blockNumber)
        }.onFailure { e ->
            val chainSyncRetry =
                chainSyncRetryHandler.findByChainAndBlockNumber(chainScanner.chainName, blockNumber) ?: ChainSyncRetry(
                    chainScanner.chainName,
                    blockNumber,
                    error = e.message,
                    maxRetries = sch.maxRetries
                )
            chainSyncRetryHandler.save(chainSyncRetry.copy(error = e.message))
            if (e is WebClientResponseException && e.statusCode == HttpStatus.TOO_MANY_REQUESTS)
                throw RateLimitException()
        }.also {
            val chainSyncRecord = chainSyncRecordHandler.lastSyncRecord(chainScanner.chainName) ?: ChainSyncRecord(
                chainScanner.chainName,
                LocalDateTime.now(),
                blockNumber
            )
            chainSyncRecordHandler.saveSyncRecord(
                chainSyncRecord.copy(syncTime = LocalDateTime.now(), blockNumber = blockNumber)
            )
        }.getOrThrow()
    }
}
