package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.bitcoin.data.BlockResponse
import co.nilin.opex.chainscan.bitcoin.proxy.GetBlockProxy
import co.nilin.opex.chainscan.core.exceptions.RateLimitException
import co.nilin.opex.chainscan.core.spi.BlockchainGateway
import kotlinx.coroutines.coroutineScope
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigInteger

@Service
class BlockchainGatewayImpl(private val proxy: GetBlockProxy) : BlockchainGateway<BlockResponse> {
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