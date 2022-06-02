package co.nilin.opex.chainscanner.tron.service

import co.nilin.opex.chainscanner.core.exceptions.RateLimitException
import co.nilin.opex.chainscanner.core.spi.ChainService
import co.nilin.opex.chainscanner.tron.data.BlockResponse
import co.nilin.opex.chainscanner.tron.proxy.TronGridProxy
import kotlinx.coroutines.coroutineScope
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigInteger

@Service
class ChainServiceImpl(private val proxy: TronGridProxy) : ChainService<BlockResponse> {
    override suspend fun getTransactions(blockNumber: BigInteger): BlockResponse = coroutineScope {
        runCatching { proxy.getBlockByNumber(blockNumber.toLong()) }.onFailure { e ->
            (e as? WebClientResponseException)?.takeIf { it.statusCode == HttpStatus.TOO_MANY_REQUESTS }
                ?.apply { throw RateLimitException(e.message) }
        }.getOrThrow() ?: throw IllegalStateException()
    }

    override suspend fun getLatestBlock(): BigInteger {
        return proxy.getLatestBlock()?.blockHeader?.rawData?.number?.toBigInteger() ?: throw IllegalStateException()
    }
}
