package co.nilin.opex.chainscan.ethereum.impl

import co.nilin.opex.chainscan.core.spi.ChainEndpointHandler
import co.nilin.opex.chainscan.ethereum.api.Web3ClientBuilder
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.web3j.protocol.core.methods.response.EthBlockNumber
import java.math.BigInteger

private class ChainServiceTest {
    private val web3ClientBuilder: Web3ClientBuilder = mockk()
    private val chainEndpointHandler: ChainEndpointHandler = mockk()
    private val chainService: ChainService = ChainService(chainEndpointHandler, web3ClientBuilder)

    @Test
    fun given_when_then(): Unit = runBlocking {
        coEvery { web3ClientBuilder.getWeb3Client() } returns mockk {
            every { ethBlockNumber() } returns mockk {
                val blockNumber = EthBlockNumber().also { it.result = "0" }
                every { send() } returns blockNumber
            }
        }
        chainService.getTransactions(BigInteger.ONE, BigInteger.ONE)
    }
}
