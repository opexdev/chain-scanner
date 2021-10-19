package co.nilin.opex.chainscan.eth.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.TransfersResult
import co.nilin.opex.chainscan.core.spi.Chain
import co.nilin.opex.chainscan.core.spi.Interpreter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.EthBlock

@Service
class ChainService(
    private val web3j: Web3j,
    private val interpreter: Interpreter<EthBlock.TransactionObject>
) : Chain {

    private val startBlockLimit = 11251897L
    private val logger = LoggerFactory.getLogger(ChainService::class.java)

    override suspend fun getTransfers(startBlock: Long, endBlock: Long?, addresses: List<String>?): TransfersResult {
        logger.info("start fetching ethereum transfers: startBlock=$startBlock, endBlock=$endBlock")
        val transfers = mutableListOf<Transfer>()
        var last: Long
        coroutineScope {
            val first = if (startBlock < startBlockLimit) startBlockLimit else startBlock
            last = endBlock ?: web3j.ethBlockNumber().send().blockNumber.toLong()

            for (i in first until last + 1) {
                launch(Dispatchers.IO) {
                    val blockNumber = DefaultBlockParameterNumber(i)
                    val block = web3j.ethGetBlockByNumber(blockNumber, true).send().block
                    block?.transactions?.forEach {
                        val tx = it as EthBlock.TransactionObject
                        val transfer = interpreter.interpret(tx)
                        if (transfer != null) {
                            if (!transfer.isTokenTransfer || addresses?.contains(transfer.token) == true) {
                                transfers.add(transfer)
                            }
                        }
                    }
                }
            }
        }
        return TransfersResult(last, transfers)
    }
}
