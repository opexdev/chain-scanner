package co.nilin.opex.chainscan.ethereum.impl

import co.nilin.opex.chainscan.core.exceptions.RateLimitException
import co.nilin.opex.chainscan.core.spi.FetchTransaction
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.exceptions.ClientConnectionException
import reactor.kotlin.core.publisher.toMono
import java.math.BigInteger

@Service
class ChainService(private val web3j: Web3j) : FetchTransaction<List<EthBlock.TransactionObject>> {
    override suspend fun getTransactions(blockNumber: BigInteger): List<EthBlock.TransactionObject> = coroutineScope {
        runCatching {
            val bn = DefaultBlockParameterNumber(blockNumber)
            web3j.ethGetBlockByNumber(bn, true).sendAsync().toMono().awaitSingle().block
        }.map {
            it.transactions.filterIsInstance<EthBlock.TransactionObject>().toList()
        }.onFailure { e ->
            when (e) {
                is ClientConnectionException -> throw RateLimitException(e.message)
            }
        }.getOrThrow()
    }
}
