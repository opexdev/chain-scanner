package co.nilin.opex.chainscanner.bitcoin.service

import co.nilin.opex.chainscanner.bitcoin.data.BlockResponse
import co.nilin.opex.chainscanner.bitcoin.proxy.GetBlockProxy
import co.nilin.opex.chainscanner.core.exceptions.RateLimitException
import co.nilin.opex.chainscanner.core.spi.ChainService
import kotlinx.coroutines.coroutineScope
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigInteger

@Service
class ChainServiceImpl(private val proxy: GetBlockProxy) : ChainService<BlockResponse> {
    override suspend fun getTransactions(blockNumber: BigInteger): BlockResponse = coroutineScope {
        val blockHash = kotlin.runCatching { proxy.getBlockHash(blockNumber.toLong()) }.onFailure { e ->
            (e as? WebClientResponseException)?.takeIf { it.statusCode == HttpStatus.TOO_MANY_REQUESTS }
                ?.apply { throw RateLimitException(e.message) }
        }.getOrThrow()
        proxy.getBlockData(blockHash) ?: throw IllegalStateException()
    }

    override suspend fun getLatestBlock(): BigInteger {
        return proxy.getInfo()?.blocks?.toBigInteger() ?: throw IllegalStateException()
    }
}
