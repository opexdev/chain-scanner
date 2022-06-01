package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.exceptions.RateLimitException
import co.nilin.opex.chainscan.core.spi.FetchTransaction
import co.nilin.opex.chainscan.tron.data.BlockResponse
import co.nilin.opex.chainscan.tron.proxy.TronGridProxy
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigInteger

@Service
class RestChainService(private val proxy: TronGridProxy) : FetchTransaction<BlockResponse> {
    override suspend fun getTransactions(blockNumber: BigInteger): BlockResponse = coroutineScope {
        runCatching { proxy.getBlockByNumber(blockNumber.toLong()) }.onFailure { e ->
            when (e) {
                is WebClientResponseException -> e.takeIf { it.rawStatusCode == 429 }
                    ?.apply { throw RateLimitException(e.message) }
            }
        }.getOrThrow() ?: throw IllegalStateException()
    }
}
