package co.nilin.opex.chainscanner.bitcoin.service

import co.nilin.opex.chainscanner.bitcoin.data.BlockResponse
import co.nilin.opex.chainscanner.bitcoin.proxy.GetBlockProxy
import co.nilin.opex.chainscanner.core.spi.ChainService
import co.nilin.opex.chainscanner.core.utils.ExceptionHandling
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class ChainServiceImpl(private val proxy: GetBlockProxy) : ChainService<BlockResponse> {
    override suspend fun getTransactions(blockNumber: BigInteger): BlockResponse = runCatching {
        proxy.getBlockHash(blockNumber.toLong())
    }.onFailure(ExceptionHandling::handleRateLimit).map {
        proxy.getBlockData(it) ?: throw IllegalStateException()
    }.getOrThrow()

    override suspend fun getLatestBlock(): BigInteger = runCatching {
        proxy.getInfo()?.blocks?.toBigInteger() ?: throw IllegalStateException()
    }.onFailure(ExceptionHandling::handleRateLimit).getOrThrow()
}
