package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.spi.GetBlockNumber
import co.nilin.opex.chainscan.tron.proxy.TronGridProxy
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class GetBlockNumberImpl(private val proxy: TronGridProxy) : GetBlockNumber {
    override suspend fun invoke(): BigInteger {
        return run { proxy.getLatestBlock() }?.blockHeader?.rawData?.number?.toBigInteger()
            ?: throw IllegalStateException()
    }
}