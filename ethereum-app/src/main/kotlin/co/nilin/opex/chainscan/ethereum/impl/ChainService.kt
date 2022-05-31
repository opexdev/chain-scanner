package co.nilin.opex.chainscan.ethereum.impl

import co.nilin.opex.chainscan.core.spi.FetchTransaction
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.EthBlock
import reactor.kotlin.core.publisher.toMono
import java.math.BigInteger

@Service
class ChainService(private val web3j: Web3j) : FetchTransaction<List<EthBlock.TransactionObject>> {
    override suspend fun getTransactions(blockNumber: BigInteger): List<EthBlock.TransactionObject> = coroutineScope {
        web3j.ethGetBlockByNumber(DefaultBlockParameterNumber(blockNumber), true).sendAsync().toMono()
            .awaitSingle().block.transactions.filterIsInstance<EthBlock.TransactionObject>().toList()
    }
}
