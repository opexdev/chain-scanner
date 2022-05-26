package co.nilin.opex.chainscan.eth.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.Decoder
import co.nilin.opex.chainscan.core.spi.FetchAndConvert
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
class ChainService(
    private val web3j: Web3j,
    private val decoder: Decoder<EthBlock.TransactionObject>
) : FetchAndConvert {
    private val logger = LoggerFactory.getLogger(ChainService::class.java)

    override suspend fun fetchAndConvert(
        startBlock: BigInteger,
        endBlock: BigInteger,
        tokenAddresses: List<String>
    ): List<Transfer> = coroutineScope {
        logger.info("Requested blocks: startBlock=$startBlock, endBlock=$endBlock")
        val transfers = LinkedBlockingQueue<Transfer>()
        logger.info("Start fetching ethereum transfers: startBlock=$startBlock, endBlock=$endBlock")
        for (i in startBlock.toLong()..endBlock.toLong()) {
            launch(Dispatchers.IO) {
                val blockNumber = DefaultBlockParameterNumber(i)
                val block = web3j.ethGetBlockByNumber(blockNumber, true).send().block
                logger.info("Fetched block $i with ${block.transactions.size} transactions")
                block?.transactions?.forEach {
                    val tx = it as EthBlock.TransactionObject
                    val transfer = decoder.invoke(tx)
                    if (!transfer.isTokenTransfer || tokenAddresses.contains(transfer.tokenAddress)) {
                        transfers.add(transfer)
                    }
                }
            }
        }
        logger.info("Finished fetching transactions: lastBlock=$endBlock transfers=${transfers.size}")
        return@coroutineScope transfers.toList()
    }
}
