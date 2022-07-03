package co.nilin.opex.chainscanner.scheduler.service

import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRetry
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncRetryHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScannerProxy
import co.nilin.opex.chainscanner.scheduler.core.spi.WebhookCaller
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class GetTransfersSubTask(
    private val scannerProxy: ScannerProxy,
    private val webhookCaller: WebhookCaller,
    private val chainSyncRetryHandler: ChainSyncRetryHandler
) {
    suspend fun fetch(sch: ChainSyncSchedule, chainScanner: ChainScanner, blockNumber: BigInteger) = runCatching {
        scannerProxy.getTransfers(chainScanner.url, blockNumber)
    }.mapCatching {
        webhookCaller.callWebhook(chainScanner.chainName, it)
    }.onFailure {
        retry(chainScanner, blockNumber, it.message, sch)
    }.mapCatching {
        scannerProxy.clearCache(chainScanner.url, blockNumber)
    }

    private suspend fun retry(
        chainScanner: ChainScanner,
        blockNumber: BigInteger,
        error: String?,
        sch: ChainSyncSchedule
    ): Boolean {
        val chainSyncRetry =
            chainSyncRetryHandler.findByChainAndBlockNumber(chainScanner.chainName, blockNumber)?.let {
                it.copy(retries = it.retries + 1, giveUp = it.retries + 1 >= it.maxRetries)
            } ?: ChainSyncRetry(chainScanner.chainName, blockNumber, maxRetries = sch.maxRetries)
        chainSyncRetryHandler.save(chainSyncRetry.copy(error = error))
        return chainSyncRetry.giveUp
    }
}
