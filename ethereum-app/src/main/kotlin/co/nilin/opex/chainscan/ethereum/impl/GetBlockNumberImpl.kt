package co.nilin.opex.chainscan.ethereum.impl

import co.nilin.opex.chainscan.core.spi.GetBlockNumber
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import java.math.BigInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Component
class GetBlockNumberImpl(private val web3j: Web3j) : GetBlockNumber {
    override suspend fun invoke(): BigInteger = suspendCoroutine {
        web3j.ethBlockNumber().sendAsync().thenAccept { blockNumber ->
            it.resume(blockNumber.blockNumber)
        }
    }
}
