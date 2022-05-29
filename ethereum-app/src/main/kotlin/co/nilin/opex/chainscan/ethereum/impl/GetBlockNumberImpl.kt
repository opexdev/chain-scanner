package co.nilin.opex.chainscan.ethereum.impl

import co.nilin.opex.chainscan.core.spi.GetBlockNumber
import co.nilin.opex.chainscan.ethereum.api.Web3ClientBuilder
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import java.math.BigInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Component
class GetBlockNumberImpl(private val web3ClientBuilder: Web3ClientBuilder) : GetBlockNumber {
    override suspend fun invoke(): BigInteger = coroutineScope {
        val web3j = web3ClientBuilder.getWeb3Client()
        suspendCoroutine {
            web3j.ethBlockNumber().sendAsync().thenAccept { blockNumber ->
                it.resume(blockNumber.blockNumber)
            }
        }
    }
}
