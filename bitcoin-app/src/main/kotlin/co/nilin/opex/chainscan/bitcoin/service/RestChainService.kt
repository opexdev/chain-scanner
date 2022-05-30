package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.bitcoin.coroutines.Dispatchers
import co.nilin.opex.chainscan.bitcoin.data.BlockResponse
import co.nilin.opex.chainscan.bitcoin.proxy.GetBlockProxy
import co.nilin.opex.chainscan.core.spi.FetchTransaction
import co.nilin.opex.chainscan.core.utils.LoggerDelegate
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class RestChainService(private val proxy: GetBlockProxy) : FetchTransaction<BlockResponse> {
    private val logger: Logger by LoggerDelegate()

    override suspend fun getTransactions(blockRange: LongRange): List<BlockResponse> {
        return coroutineScope {
            blockRange.asFlow().flowOn(Dispatchers.SYNC).map {
                async { runCatching { proxy.getBlockHash(it) }.getOrNull() }
            }.buffer().mapNotNull { it.await() }.mapNotNull {
                proxy.getBlockData(it)
            }.toList()
        }
    }
}
