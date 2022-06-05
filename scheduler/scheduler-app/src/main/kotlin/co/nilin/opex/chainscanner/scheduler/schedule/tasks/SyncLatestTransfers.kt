package co.nilin.opex.chainscanner.scheduler.schedule.tasks

import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRecord
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRetry
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.*
import co.nilin.opex.chainscanner.scheduler.exceptions.RateLimitException
import co.nilin.opex.chainscanner.scheduler.exceptions.ScannerConnectException
import co.nilin.opex.chainscanner.scheduler.service.BlockRangeCalculator
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigInteger
import java.net.ConnectException
import java.time.LocalDateTime

@Service
class SyncLatestTransfers(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller,
    private val blockRangeCalculator: BlockRangeCalculator
) : ScheduleTask, SyncScheduleTaskBase(chainSyncSchedulerHandler) {
    private val logger: Logger by LoggerDelegate()

    override suspend fun execute(sch: ChainSyncSchedule) {
        val chainScanner = chainScannerHandler.getScannersByName(sch.chainName).firstOrNull() ?: return
        val blockRange = runCatching {
            blockRangeCalculator.calculateBlockRange(chainScanner, sch.confirmations)
        }.onFailure(::rethrowBlockRangeExceptions).getOrThrow()
        logger.debug("Fetch transfers on block range: ${blockRange.first} - ${blockRange.last}")
        runCatching {
            coroutineScope {
                val br = blockRange.take(chainScanner.maxBlockRange)
                br.forEach { bn ->
                    launch { fetch(sch, chainScanner, bn.toBigInteger()) }
                }
            }
        }.onFailure { e ->
            rethrowScheduleExceptions(e, sch, chainScanner)
        }.onSuccess {
            sch.enqueueNextSchedule(sch.delay)
            logger.trace("Successfully fetched transfers for block range: ${blockRange.first} - ${blockRange.last}")
        }
    }

    private suspend fun fetch(sch: ChainSyncSchedule, chainScanner: ChainScanner, blockNumber: BigInteger) {
        runCatching {
            scannerProxy.getTransfers(chainScanner.url, blockNumber)
        }.onFailure {
            if (it is WebClientResponseException && it.isTooManyRequests) throw RateLimitException()
            else if (it is WebClientRequestException && it.isConnectionError) throw ScannerConnectException("Get transfers")
            else enqueueRetryTask(chainScanner, blockNumber, it.message, sch)
        }.mapCatching {
            webhookCaller.callWebhook(chainScanner.chainName, it)
        }.onFailure { e ->
            enqueueRetryTask(chainScanner, blockNumber, e.message, sch)
        }.mapCatching {
            scannerProxy.clearCache(chainScanner.url, blockNumber)
        }.onFailure {
            if (it is WebClientRequestException && it.isConnectionError) throw ScannerConnectException("Clear cache")
        }.also {
            updateChainSyncRecord(chainScanner.chainName, blockNumber)
        }.getOrThrow()
    }

    private fun rethrowBlockRangeExceptions(e: Throwable) {
        if (e is WebClientRequestException && e.isConnectionError) throw ScannerConnectException("Block range")
    }

    private suspend fun enqueueRetryTask(
        chainScanner: ChainScanner,
        blockNumber: BigInteger,
        error: String?,
        sch: ChainSyncSchedule
    ) {
        val chainSyncRetry = chainSyncRetryHandler.findByChainAndBlockNumber(chainScanner.chainName, blockNumber)
            ?: ChainSyncRetry(chainScanner.chainName, blockNumber, error = error, maxRetries = sch.maxRetries)
        chainSyncRetryHandler.save(chainSyncRetry.copy(error = error))
    }

    private suspend fun updateChainSyncRecord(chainName: String, blockNumber: BigInteger) {
        val chainSyncRecord = chainSyncRecordHandler.lastSyncRecord(chainName)
            ?: ChainSyncRecord(chainName, LocalDateTime.now(), blockNumber)
        chainSyncRecordHandler.saveSyncRecord(
            chainSyncRecord.copy(syncTime = LocalDateTime.now(), blockNumber = blockNumber)
        )
    }

    private val WebClientResponseException.isTooManyRequests: Boolean
        get() = statusCode == HttpStatus.TOO_MANY_REQUESTS

    private val WebClientRequestException.isConnectionError: Boolean
        get() = mostSpecificCause is ConnectException
}
