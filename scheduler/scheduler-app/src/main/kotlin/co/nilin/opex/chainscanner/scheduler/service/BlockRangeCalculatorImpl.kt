package co.nilin.opex.chainscanner.scheduler.service

import co.nilin.opex.chainscanner.scheduler.api.BlockRangeCalculator
import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncRecordHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScannerProxy
import co.nilin.opex.chainscanner.scheduler.exceptions.ScannerConnectException
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientRequestException
import java.math.BigInteger
import java.net.ConnectException

@Service
class BlockRangeCalculatorImpl(
    private val scannerProxy: ScannerProxy,
    private val chainSyncRecordHandler: ChainSyncRecordHandler
) : BlockRangeCalculator {
    private val logger: Logger by LoggerDelegate()

    override suspend fun calculateBlockRange(chainScanner: ChainScanner, confirmations: Int): LongRange = runCatching {
        val chainHeadBlock = scannerProxy.getBlockNumber(chainScanner.url)
        val confirmedBlock = chainHeadBlock - confirmations.toBigInteger()
        val lastSyncedBlock = chainSyncRecordHandler.lastSyncedBlockedNumber(chainScanner.chainName)
        val startBlock = lastSyncedBlock?.plus(BigInteger.ONE) ?: confirmedBlock
        val endBlock = confirmedBlock.min(startBlock + chainScanner.maxBlockRange.toBigInteger())
        startBlock.toLong()..endBlock.toLong()
    }.onFailure { e ->
        if (e is WebClientRequestException && e.isConnectionError) throw ScannerConnectException("Block range")
    }.getOrThrow()

    private val WebClientRequestException.isConnectionError: Boolean
        get() = mostSpecificCause is ConnectException
}
