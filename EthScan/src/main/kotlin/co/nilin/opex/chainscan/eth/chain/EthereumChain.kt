package co.nilin.opex.chainscan.eth.chain

import co.nilin.opex.chainscan.model.Transfer
import co.nilin.opex.chainscan.eth.spi.Chain
import co.nilin.opex.chainscan.eth.spi.Interpreter
import org.springframework.beans.factory.annotation.Value
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.http.HttpService

class EthereumChain(private val interpreter: Interpreter<EthBlock.TransactionObject>) : Chain {
    @Value("\${blockchain.url}")
    private lateinit var url: String
    private val web3j: Web3j = Web3j.build(HttpService(url))

    override fun getTransfers(startBlock: Long, endBlock: Long, addresses: List<String>): List<Transfer> {
        TODO("Not yet implemented")
    }
}
