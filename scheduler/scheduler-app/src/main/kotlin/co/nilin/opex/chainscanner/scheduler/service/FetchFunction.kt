package co.nilin.opex.chainscanner.scheduler.service

import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRetry
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncRetryHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScannerProxy
import co.nilin.opex.chainscanner.scheduler.core.spi.WebhookCaller
import co.nilin.opex.chainscanner.scheduler.exceptions.RateLimitException
import co.nilin.opex.chainscanner.scheduler.exceptions.ScannerConnectException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigInteger
import java.net.ConnectException

@Service
class FetchFunction(
    private val scannerProxy: ScannerProxy,
    private val webhookCaller: WebhookCaller,
    private val chainSyncRetryHandler: ChainSyncRetryHandler
) {
    suspend fun fetch(sch: ChainSyncSchedule, chainScanner: ChainScanner, blockNumber: BigInteger) = runCatching {
        scannerProxy.getTransfers(chainScanner.url, blockNumber)
    }.recoverCatching { e ->
        if (e is WebClientResponseException && e.isTooManyRequests) throw RateLimitException()
        else if (e is WebClientRequestException && e.isConnectionError) throw ScannerConnectException("Get transfers")
        else throw e
    }.mapCatching {
        webhookCaller.callWebhook(chainScanner.chainName, it)
    }.onFailure { e ->
        retry(chainScanner, blockNumber, e.message, sch)
    }.mapCatching {
        scannerProxy.clearCache(chainScanner.url, blockNumber)
    }.onFailure {
        if (it is WebClientRequestException && it.isConnectionError) throw ScannerConnectException("Clear cache")
    }

    private suspend fun retry(
        chainScanner: ChainScanner,
        blockNumber: BigInteger,
        error: String?,
        sch: ChainSyncSchedule
    ) {
        val chainSyncRetry =
            chainSyncRetryHandler.findByChainAndBlockNumber(chainScanner.chainName, blockNumber)?.let {
                it.copy(retries = it.retries + 1, giveUp = it.retries + 1 >= it.maxRetries)
            } ?: ChainSyncRetry(chainScanner.chainName, blockNumber, maxRetries = sch.maxRetries)
        chainSyncRetryHandler.save(chainSyncRetry.copy(error = error))
    }

    private val WebClientResponseException.isTooManyRequests: Boolean
        get() = statusCode == HttpStatus.TOO_MANY_REQUESTS

    private val WebClientRequestException.isConnectionError: Boolean
        get() = mostSpecificCause is ConnectException
}
