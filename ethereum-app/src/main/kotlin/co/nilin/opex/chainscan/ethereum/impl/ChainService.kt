package co.nilin.opex.chainscan.ethereum.impl

import co.nilin.opex.chainscan.core.spi.FetchTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigInteger
import java.util.concurrent.LinkedBlockingQueue

@Service
class ChainService(private val web3j: Web3j) : FetchTransaction<EthBlock.TransactionObject> {
    private val logger = LoggerFactory.getLogger(ChainService::class.java)

    override suspend fun getTransactions(
        startBlock: BigInteger,
        endBlock: BigInteger
    ): List<EthBlock.TransactionObject> {
        logger.info("Requested blocks: startBlock=$startBlock, endBlock=$endBlock")
        val transactions = LinkedBlockingQueue<EthBlock.TransactionObject>()
        logger.info("Start fetching ethereum transfers: startBlock=$startBlock, endBlock=$endBlock")
        coroutineScope {
            for (i in startBlock.toLong()..endBlock.toLong()) {
                launch(Dispatchers.IO) {
                    val blockNumber = DefaultBlockParameterNumber(i)
                    val block = web3j.ethGetBlockByNumber(blockNumber, true).send().block
                    logger.info("Fetched block $i with ${block.transactions.size} transactions")
                    block?.transactions?.forEach {
                        transactions.add(it as EthBlock.TransactionObject)
                    }
                }
            }
        }
        logger.info("Finished fetching transactions: lastBlock=$endBlock transfers=${transactions.size}")
        return transactions.toList()
    }
}
