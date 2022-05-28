package co.nilin.opex.chainscan.core.service

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import java.math.BigInteger

class ChainSyncService<T>(
    @Value("\$chain-name") private val chainName: String,
    private val fetchTransaction: FetchTransaction<T>,
    private val decoder: Decoder<T>,
    private val getBlockNumber: GetBlockNumber,
    private val watchListHandler: WatchListHandler,
    private val transferCacheHandler: TransferCacheHandler,
) {
    private val logger = LoggerFactory.getLogger(ChainSyncService::class.java)

    suspend fun getTransfers(start: BigInteger?, end: BigInteger?): List<Transfer> {
        val actualStart = start ?: getBlockNumber.invoke()
        val actualEnd = (end ?: getBlockNumber.invoke()).takeIf { it > actualStart } ?: actualStart
        val tokens = watchListHandler.findAll().map { impl -> impl.address }.toList()
        logger.info("Syncing for: $chainName - Block: $actualStart")
        val cached = transferCacheHandler.getTransfers(tokens)
        val notCachedStartBlock =
            cached.filter { it.blockNumber >= actualStart }.maxOfOrNull { it.blockNumber }?.plus(BigInteger.ONE)
                ?: actualStart
        val response = fetchTransaction.getTransactions(notCachedStartBlock, actualEnd)
        val transfers = response.flatMap { decoder.invoke(it) }.filter {
            !it.isTokenTransfer || tokens.contains(it.tokenAddress)
        }
        transferCacheHandler.saveTransfers(transfers)
        return cached + transfers
    }
}
