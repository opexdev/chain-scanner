package co.nilin.opex.chainscan.core.service

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.*
import co.nilin.opex.chainscan.core.utils.LoggerDelegate
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class ChainSyncService<T>(
    @Value("\${app.chain-name}") private val chainName: String,
    private val fetchTransaction: FetchTransaction<T>,
    private val decoder: Decoder<T>,
    private val getBlockNumber: GetBlockNumber,
    private val watchListHandler: WatchListHandler,
    private val transferCacheHandler: TransferCacheHandler,
    private val addressAdapter: AddressAdapter
) {
    private val logger: Logger by LoggerDelegate()

    suspend fun getTransfers(start: BigInteger? = null, end: BigInteger? = null): List<Transfer> {
        require(start?.abs() == start)
        val currentBlockNumber = getBlockNumber.invoke()
        val actualStart = start ?: currentBlockNumber
        val adjustedEnd = end ?: currentBlockNumber
        val actualEnd = adjustedEnd.takeIf { it >= BigInteger.ZERO } ?: (currentBlockNumber + end!!)
        val tokens = watchListHandler.findAll().map { addressAdapter.makeValid(it.address) }
        logger.info("Syncing for: $chainName - Block: $actualStart")
        val cached = transferCacheHandler.getTransfers(tokens)
        val notCachedStartBlock =
            cached.filter { it.blockNumber >= actualStart }.maxOfOrNull { it.blockNumber }?.plus(BigInteger.ONE)
                ?: actualStart
        logger.info("Start fetching bitcoin transfers: startBlock=$actualStart, endBlock=$actualEnd")
        val blockRange = notCachedStartBlock.toLong()..actualEnd.toLong()
        val response = fetchTransaction.getTransactions(blockRange)
        logger.info("Finished fetching transactions: lastBlock=$actualStart transfers=${response.size}")
        val transfers =
            response.flatMap { decoder.invoke(it) }.filter { !it.isTokenTransfer || tokens.contains(it.tokenAddress) }
        transferCacheHandler.saveTransfers(transfers)
        return cached + transfers
    }
}
