package co.nilin.opex.chainscan.scheduler.jobs

import co.nilin.opex.chainscan.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscan.scheduler.core.po.ChainSyncRecord
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
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class SyncLatestTransfersScheduledJob(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller
) : ScheduledJob {
    private val logger: Logger by LoggerDelegate()

    override suspend fun execute(sch: ChainSyncSchedule) {
        val chainScanner = chainScannerHandler.getScannersByName(sch.chainName).first()
        val blockRange = calculateBlockRange(chainScanner, sch.confirmations)
        logger.trace("Fetch transfers on block range: ${blockRange.first} - ${blockRange.last}")
        runCatching {
            coroutineScope {
                val br = blockRange.chunked(chainScanner.maxBlockRange).firstOrNull()
                br?.forEach { bn ->
                    launch { fetch(chainScanner, bn.toBigInteger(), sch) }
                }
            }
        }.onFailure { e ->
            when (e) {
                is WebClientResponseException -> e.takeIf { it.statusCode == HttpStatus.TOO_MANY_REQUESTS }
                    ?.run { sch.enqueueNextSchedule(chainScanner.delayOnRateLimit.toLong()) }
                    ?: sch.enqueueNextSchedule(sch.errorDelay)
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
        chainSyncSchedulerHandler.save(copy(retryTime = retryTime))
    }

    private suspend fun fetch(chainScanner: ChainScanner, blockNumber: BigInteger, sch: ChainSyncSchedule) {
        runCatching {
            val response = scannerProxy.getTransfers(chainScanner.url, blockNumber)
            webhookCaller.callWebhook(chainScanner.chainName, response)
        }.onFailure { e ->
            val chainSyncRetry = chainSyncRetryHandler.findByChainAndBlockNumber(chainScanner.chainName, blockNumber)
            chainSyncRetry?.let { chainSyncRetryHandler.save(it.copy(error = e.message)) }
                ?: chainSyncRetryHandler.save(
                    ChainSyncRetry(
                        chainScanner.chainName,
                        blockNumber,
                        error = e.message,
                        maxRetries = sch.maxRetries
                    )
                )
            when (e) {
                is WebClientResponseException -> e.takeUnless { it.statusCode == HttpStatus.TOO_MANY_REQUESTS }
                    ?: throw e
            }
        }.also {
            val record = chainSyncRecordHandler.lastSyncRecord(chainScanner.chainName) ?: ChainSyncRecord(
                chainScanner.chainName,
                LocalDateTime.now(),
                blockNumber
            )
            chainSyncRecordHandler.saveSyncRecord(
                record.copy(
                    syncTime = LocalDateTime.now(),
                    blockNumber = blockNumber
                )
            )
        }.getOrThrow()
    }
}
