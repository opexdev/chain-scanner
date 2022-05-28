package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.spi.GetBlockNumber
import co.nilin.opex.chainscan.tron.proxy.TronGridProxy
import java.math.BigInteger

class GetBlockNumberImpl(private val proxy: TronGridProxy) : GetBlockNumber {
    override suspend fun invoke(): BigInteger {
        return run { proxy.getLatestBlock() }?.blockHeader?.rawData?.number?.toBigInteger()
            ?: throw IllegalStateException()
    }
}