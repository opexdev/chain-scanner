package co.nilin.opex.chainscan.core.service

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.Decoder
import co.nilin.opex.chainscan.core.spi.FetchTransaction
import co.nilin.opex.chainscan.core.spi.TransferCacheHandler
import co.nilin.opex.chainscan.core.spi.WatchListHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import java.math.BigInteger

class ChainSyncService<T>(
    @Value("\$chain-name") private val chainName: String,
    private val fetchTransaction: FetchTransaction<T>,
    private val decoder: Decoder<T>,
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
        val response = fetchTransaction.getTransactions(notCachedStartBlock, end)
        val transfers = response.map { decoder.invoke(it) }.filter {
            !it.isTokenTransfer || tokens.contains(it.tokenAddress)
        }
        transferCacheHandler.saveTransfers(transfers)
        return cached + transfers
    }
}
