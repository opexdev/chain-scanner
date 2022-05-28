package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.spi.FetchTransaction
import co.nilin.opex.chainscan.tron.data.TransactionResponse
import co.nilin.opex.chainscan.tron.proxy.TronGridProxy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class RestChainService(private val proxy: TronGridProxy) : FetchTransaction<TransactionResponse> {
    private val logger = LoggerFactory.getLogger(RestChainService::class.java)

    override suspend fun getTransactions(startBlock: BigInteger, endBlock: BigInteger): List<TransactionResponse> {
        logger.info("Requested blocks: startBlock=$startBlock, endBlock=$endBlock")
        logger.info("Start fetching tron transfers: startBlock=$startBlock, endBlock=$endBlock")
        val transactions = ArrayList<TransactionResponse>()
        for (i in startBlock.toLong()..endBlock.toLong()) {
            runCatching { proxy.getBlockByNumber(i) }.getOrNull()?.let { d -> transactions.addAll(d.transactions) }
        }
        return transactions
    }
}
