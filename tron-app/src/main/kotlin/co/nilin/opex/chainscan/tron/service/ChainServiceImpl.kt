package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.exceptions.RateLimitException
import co.nilin.opex.chainscan.core.spi.ChainService
import co.nilin.opex.chainscan.tron.data.BlockResponse
import co.nilin.opex.chainscan.tron.proxy.TronGridProxy
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
