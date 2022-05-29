package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.spi.FetchTransaction
import co.nilin.opex.chainscan.core.utils.LoggerDelegate
import co.nilin.opex.chainscan.tron.data.BlockResponse
import co.nilin.opex.chainscan.tron.proxy.TronGridProxy
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class RestChainService(private val proxy: TronGridProxy) : FetchTransaction<BlockResponse> {
    private val logger: Logger by LoggerDelegate()

    override suspend fun getTransactions(startBlock: BigInteger, endBlock: BigInteger): List<BlockResponse> {
        logger.info("Requested blocks: startBlock=$startBlock, endBlock=$endBlock")
        logger.info("Start fetching tron transfers: startBlock=$startBlock, endBlock=$endBlock")
        val blocks = ArrayList<BlockResponse>()
        for (i in startBlock.toLong()..endBlock.toLong()) {
            runCatching { proxy.getBlockByNumber(i) }.getOrNull()?.let { d -> blocks.add(d) }
        }
        return blocks
    }
}
