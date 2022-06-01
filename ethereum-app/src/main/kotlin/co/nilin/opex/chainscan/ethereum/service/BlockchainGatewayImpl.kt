package co.nilin.opex.chainscan.ethereum.service

import co.nilin.opex.chainscan.core.exceptions.RateLimitException
import co.nilin.opex.chainscan.core.spi.BlockchainGateway
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
class BlockchainGatewayImpl(private val web3j: Web3j) : BlockchainGateway<List<EthBlock.TransactionObject>> {
    override suspend fun getTransactions(blockNumber: BigInteger): List<EthBlock.TransactionObject> = coroutineScope {
        runCatching {
            val bn = DefaultBlockParameterNumber(blockNumber)
            web3j.ethGetBlockByNumber(bn, true).sendAsync().toMono().awaitSingle().block
        }.onFailure { e ->
            when (e) {
                is ClientConnectionException -> throw RateLimitException(e.message)
            }
        }.map {
            it.transactions.filterIsInstance<EthBlock.TransactionObject>().toList()
        }.getOrThrow()
    }

    override suspend fun getLatestBlock(): BigInteger {
        return web3j.ethBlockNumber().sendAsync().toMono().awaitSingle().blockNumber
    }
}
