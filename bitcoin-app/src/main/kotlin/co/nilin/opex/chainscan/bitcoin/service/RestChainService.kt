package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.bitcoin.data.BlockResponse
import co.nilin.opex.chainscan.bitcoin.proxy.GetBlockProxy
import co.nilin.opex.chainscan.core.spi.FetchTransaction
import co.nilin.opex.chainscan.core.utils.LoggerDelegate
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
        logger.info("Requested blocks: startBlock=$startBlock, endBlock=$endBlock")
        val blockHash = ArrayList<String>()
        logger.info("Start fetching bitcoin transfers: startBlock=$startBlock, endBlock=$endBlock")
        for (i in startBlock.toLong()..endBlock.toLong()) {
            runCatching { blockHash.add(proxy.getBlockHash(i)) }
        }
        return blockHash.mapNotNull {
            logger.info("Fetching block $it")
            proxy.getBlockData(it)
        }
    }
}
