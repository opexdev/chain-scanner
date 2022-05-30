package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.spi.FetchTransaction
import co.nilin.opex.chainscan.core.utils.LoggerDelegate
import co.nilin.opex.chainscan.tron.coroutines.Dispatchers
import co.nilin.opex.chainscan.tron.data.BlockResponse
import co.nilin.opex.chainscan.tron.proxy.TronGridProxy
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class RestChainService(private val proxy: TronGridProxy) : FetchTransaction<BlockResponse> {
    private val logger: Logger by LoggerDelegate()

    override suspend fun getTransactions(startBlock: BigInteger, endBlock: BigInteger): List<BlockResponse> {
        logger.info("Start fetching tron transfers: startBlock=$startBlock, endBlock=$endBlock")
        val blockRange = startBlock.toLong()..endBlock.toLong()
        return coroutineScope {
            blockRange.asFlow().flowOn(Dispatchers.SYNC).map {
                async { runCatching { proxy.getBlockByNumber(it) }.getOrNull() }
            }.buffer().map { it.await() }.filterNotNull().toList()
        }.also {
            logger.info("Finished fetching transactions: lastBlock=$endBlock transfers=${it.size}")
        }
    }
}
