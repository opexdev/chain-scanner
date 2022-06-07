package co.nilin.opex.chainscanner.tron.service

import co.nilin.opex.chainscanner.core.spi.ChainService
import co.nilin.opex.chainscanner.core.utils.ExceptionHandling
import co.nilin.opex.chainscanner.tron.data.BlockResponse
import co.nilin.opex.chainscanner.tron.proxy.TronGridProxy
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class ChainServiceImpl(private val proxy: TronGridProxy) : ChainService<BlockResponse> {
    override suspend fun getTransactions(blockNumber: BigInteger): BlockResponse = runCatching {
        proxy.getBlockByNumber(blockNumber.toLong())
    }.onFailure(ExceptionHandling::handleRateLimit).getOrThrow() ?: throw IllegalStateException()

    override suspend fun getLatestBlock(): BigInteger = runCatching {
        proxy.getLatestBlock()?.blockHeader?.rawData?.number?.toBigInteger() ?: throw IllegalStateException()
    }.onFailure(ExceptionHandling::handleRateLimit).getOrThrow()
}
