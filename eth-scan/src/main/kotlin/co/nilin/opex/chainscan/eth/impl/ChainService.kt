package co.nilin.opex.chainscan.eth.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.TransfersRequest
import co.nilin.opex.chainscan.core.spi.FetchAndConvert
import co.nilin.opex.chainscan.core.spi.Decoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigInteger

@Service
class ChainService(
    private val web3j: Web3j,
    private val decoder: Decoder<EthBlock.TransactionObject>
) : FetchAndConvert {
    private val logger = LoggerFactory.getLogger(ChainService::class.java)

    override suspend fun fetchAndConvert(endpoint: String, request: TransfersRequest): List<Transfer> {
        val startBlock = request.startBlock!!
        val endBlock = request.endBlock

        logger.info("Requested blocks: startBlock=$startBlock, endBlock=$endBlock")

        val transfers = mutableListOf<Transfer>()
        var last: BigInteger
        coroutineScope {
            val networkBlockHeight = web3j.ethBlockNumber().send().blockNumber
            last = if (endBlock == null || endBlock > networkBlockHeight) networkBlockHeight else endBlock
            val first = if (startBlock == BigInteger.valueOf(0L) || startBlock > last)
                last - BigInteger.valueOf(10)
            else if (last - startBlock > BigInteger.valueOf(300))
                last - BigInteger.valueOf(300)
            else
                startBlock

            logger.info("Start fetching ethereum transfers: startBlock=$first, endBlock=$last")
            for (i in (first).toLong()..(last + BigInteger.ONE).toLong()) {
                launch(Dispatchers.IO) {
                    val blockNumber = DefaultBlockParameterNumber(i)
                    val block = web3j.ethGetBlockByNumber(blockNumber, true).send().block
                    logger.info("Fetched block $i with ${block.transactions.size} transactions")

                    block?.transactions?.forEach {
                        val tx = it as EthBlock.TransactionObject
                        val transfer = decoder.interpret(tx)
                        if (transfer != null) {
                            if (!transfer.isTokenTransfer || request.addresses?.contains(transfer.token?.lowercase()) == true) {
                                transfers.add(transfer)
                            }
                        }
                    }
                }
            }
        }

        logger.info("Finished fetching transactions: lastBlock=$last transfers=${transfers.size}")
        return transfers.toList()
    }
}
