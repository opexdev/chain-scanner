package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.spi.FetchTransaction
import co.nilin.opex.chainscan.tron.data.BlockResponse
import co.nilin.opex.chainscan.tron.proxy.TronGridProxy
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class RestChainService(private val proxy: TronGridProxy) : FetchTransaction<BlockResponse> {
    override suspend fun getTransactions(blockNumber: BigInteger): BlockResponse = coroutineScope {
        proxy.getBlockByNumber(blockNumber.toLong()) ?: throw IllegalStateException()
    }
}
