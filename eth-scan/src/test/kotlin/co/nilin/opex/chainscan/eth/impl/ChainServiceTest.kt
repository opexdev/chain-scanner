package co.nilin.opex.chainscan.eth.impl

import co.nilin.opex.chainscan.core.spi.Decoder
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigInteger

private class ChainServiceTest {
    private val web3j: Web3j = mockk()
    private val decoder: Decoder<EthBlock.TransactionObject> = mockk()
    private val chainService: ChainService = ChainService(web3j, decoder)

    @Test
    fun given_when_then(): Unit = runBlocking {
        chainService.fetchAndConvert("", BigInteger.ONE, BigInteger.ONE, emptyList())
    }
}
