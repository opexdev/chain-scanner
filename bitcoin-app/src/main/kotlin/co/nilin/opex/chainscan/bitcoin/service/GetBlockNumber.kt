package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.bitcoin.proxy.GetBlockProxy
import co.nilin.opex.chainscan.core.spi.GetBlockNumber
import java.math.BigInteger

class GetBlockNumberImpl(private val proxy: GetBlockProxy) : GetBlockNumber {
    override suspend fun invoke(): BigInteger {
        return proxy.getInfo()?.blocks?.toBigInteger() ?: throw IllegalStateException()
    }
}