package co.nilin.opex.chainscan.ethereum.impl

import co.nilin.opex.chainscan.ethereum.service.FetchTransactionImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.EthBlockNumber
import java.math.BigInteger

private class FetchTransactionImplTest {
    private val web3j: Web3j = mockk()
    private val fetchTransactionImpl: FetchTransactionImpl = FetchTransactionImpl(web3j)

    @Test
    fun givenBlockNumber_whenGetTransactions_thenSuccess(): Unit = runBlocking {
        every { web3j.ethBlockNumber() } returns mockk {
            val blockNumber = EthBlockNumber().also { it.result = "0" }
            every { send() } returns blockNumber
        }
        fetchTransactionImpl.getTransactions(BigInteger.ZERO)
    }
}
