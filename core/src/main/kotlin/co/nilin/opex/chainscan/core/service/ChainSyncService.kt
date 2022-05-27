package co.nilin.opex.chainscan.core.service

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.FetchAndConvert
import co.nilin.opex.chainscan.core.spi.WatchListHandler
import co.nilin.opex.chainscan.core.spi.TransferCacheHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import java.math.BigInteger

class ChainSyncService(
    @Value("\$chain-name") private val chainName: String,
    private val fetchAndConvert: FetchAndConvert,
    private val watchListHandler: WatchListHandler,
    private val transferCacheHandler: TransferCacheHandler,
) {
    private val logger = LoggerFactory.getLogger(ChainSyncService::class.java)

    suspend fun getTransfers(start: BigInteger, end: BigInteger): List<Transfer> {
        val tokens = watchListHandler.findAll().map { impl -> impl.address }.toList()
        logger.info("Syncing for: $chainName - Block: $start")
        val cached = transferCacheHandler.getTransfers(tokens)
        val notCachedStartBlock =
            cached.filter { it.blockNumber >= start }.maxOfOrNull { it.blockNumber }?.plus(BigInteger.ONE) ?: start
        val transfers = fetchAndConvert.fetchAndConvert(notCachedStartBlock, end, tokens)
        transferCacheHandler.saveTransfers(transfers)
        return cached + transfers
    }
}
