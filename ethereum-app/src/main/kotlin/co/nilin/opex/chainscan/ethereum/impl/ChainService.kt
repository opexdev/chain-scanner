package co.nilin.opex.chainscan.ethereum.impl

import co.nilin.opex.chainscan.core.spi.FetchTransaction
import co.nilin.opex.chainscan.core.utils.LoggerDelegate
import co.nilin.opex.chainscan.ethereum.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.EthBlock
import reactor.kotlin.core.publisher.toMono

@Service
class ChainService(private val web3j: Web3j) : FetchTransaction<EthBlock.TransactionObject> {
    private val logger: Logger by LoggerDelegate()

    override suspend fun getTransactions(blockRange: LongRange): List<EthBlock.TransactionObject> {
        return coroutineScope {
            blockRange.asFlow().flowOn(Dispatchers.SYNC).map {
                val blockNumber = DefaultBlockParameterNumber(it)
                async { web3j.ethGetBlockByNumber(blockNumber, true).sendAsync().toMono().awaitSingle().block }
            }.buffer().map { it.await() }.map {
                it.transactions.filterIsInstance<EthBlock.TransactionObject>()
            }.toList().flatten()
        }
    }
}
