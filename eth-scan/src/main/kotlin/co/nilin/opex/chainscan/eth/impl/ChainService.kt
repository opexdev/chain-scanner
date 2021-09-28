package co.nilin.opex.chainscan.eth.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.Chain
import co.nilin.opex.chainscan.core.spi.Interpreter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.http.HttpService

@Service
class ChainService(private val interpreter: Interpreter<EthBlock.TransactionObject>) : Chain {
    @Value("\${blockchain.url}")
    private lateinit var url: String
    private val web3j: Web3j by lazy { Web3j.build(HttpService(url)) }

    override suspend fun getTransfers(startBlock: Long, endBlock: Long, addresses: List<String>): List<Transfer> {
        val transfers = mutableListOf<Transfer>()
        coroutineScope {
            val times = (endBlock - startBlock).toInt()
            repeat(times) { i ->
                launch(Dispatchers.IO) {
                    val blockNumber = { (startBlock + i).toString() }
                    val transactions = web3j.ethGetBlockByNumber(blockNumber, true).send().block.transactions
                    transactions.forEach {
                        val tx = it as EthBlock.TransactionObject
                        val transfer = interpreter.interpret(tx)
                        if (transfer != null) {
                            if (transfer.isTokenTransfer && addresses.contains(transfer.token)) {
                                transfers.add(transfer)
                            }
                        }
                    }
                }
            }
        }
        return transfers
    }
}
