package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.bitcoin.data.BlockResponse
import co.nilin.opex.chainscan.bitcoin.proxy.GetBlockProxy
import co.nilin.opex.chainscan.core.spi.FetchTransaction
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class RestChainService(private val proxy: GetBlockProxy) : FetchTransaction<BlockResponse> {
    override suspend fun getTransactions(blockNumber: BigInteger): BlockResponse = coroutineScope {
        val blockHash = proxy.getBlockHash(blockNumber.toLong())
        proxy.getBlockData(blockHash) ?: throw IllegalStateException()
    }
}
