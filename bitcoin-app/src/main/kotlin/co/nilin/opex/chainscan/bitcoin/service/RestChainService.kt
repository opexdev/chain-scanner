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
import java.math.BigInteger

@Service
class RestChainService(private val proxy: GetBlockProxy) : FetchTransaction<BlockResponse> {
    private val logger: Logger by LoggerDelegate()

    override suspend fun getTransactions(
        startBlock: BigInteger,
        endBlock: BigInteger
    ): List<BlockResponse> {
        logger.info("Start fetching bitcoin transfers: startBlock=$startBlock, endBlock=$endBlock")
        val blockRange = startBlock.toLong()..endBlock.toLong()
        return coroutineScope {
            blockRange.asFlow().flowOn(Dispatchers.SYNC).map {
                async { runCatching { proxy.getBlockHash(it) }.getOrNull() }
            }.buffer().mapNotNull { it.await() }.mapNotNull {
                logger.info("Fetching block $it")
                proxy.getBlockData(it)
            }.toList()
        }.also {
            logger.info("Finished fetching transactions: lastBlock=$endBlock transfers=${it.size}")
        }
    }
}
