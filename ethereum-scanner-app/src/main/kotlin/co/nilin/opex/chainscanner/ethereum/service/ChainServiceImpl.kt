package co.nilin.opex.chainscanner.ethereum.service

import co.nilin.opex.chainscanner.core.spi.ChainService
import co.nilin.opex.chainscanner.ethereum.utils.ExceptionHandling
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.response.Transaction
import reactor.kotlin.core.publisher.toMono
import java.math.BigInteger

@Service
class ChainServiceImpl(private val web3j: Web3j) : ChainService<List<Transaction>> {
    override suspend fun getTransactions(blockNumber: BigInteger): List<Transaction> = runCatching {
        val bn = DefaultBlockParameterNumber(blockNumber)
        web3j.ethGetBlockByNumber(bn, true).sendAsync().toMono().awaitSingle().block
    }.onFailure(ExceptionHandling::detectRateLimit).map { block ->
        block.transactions.filterIsInstance<Transaction>().filter { !it.to.isNullOrBlank() }.toList()
    }.getOrThrow()

    override suspend fun getLatestBlock(): BigInteger = runCatching {
        web3j.ethBlockNumber().sendAsync().toMono().awaitSingle().blockNumber
    }.onFailure(ExceptionHandling::detectRateLimit).getOrThrow()

    override suspend fun getTransactionByHash(hash: String): List<Transaction> = runCatching {
        web3j.ethGetTransactionByHash(hash).sendAsync().toMono().awaitSingle()
    }.onFailure(ExceptionHandling::detectRateLimit).map {
        listOf(it.transaction.get())
    }.getOrThrow()
}
