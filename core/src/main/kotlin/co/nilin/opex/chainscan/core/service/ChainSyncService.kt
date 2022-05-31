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

    suspend fun getTransfers(blockNumber: BigInteger? = null): List<Transfer> {
        require(blockNumber?.abs() == blockNumber)
        val actualBlockNumber = actualBlockNumber(blockNumber)
        val watchedTokens = watchListHandler.findAll().map { addressAdapter.makeValid(it.address) }
        logger.info("Syncing for: $chainName - Block: $actualBlockNumber")
        val cached = transferCacheHandler.getTransfers(watchedTokens, actualBlockNumber)
        return if (cached.isEmpty()) {
            logger.info("Start fetching $chainName transfers: blockNumber=$blockNumber")
            val response = fetchTransaction.getTransactions(actualBlockNumber)
            logger.info("Finished fetching block info: blockNumber=$blockNumber")
            return decoder.invoke(response).filter {
                !it.isTokenTransfer || watchedTokens.contains(it.tokenAddress)
            }.also {
                transferCacheHandler.saveTransfers(it)
            }
        } else {
            logger.info("Loading $chainName transfers from cache: blockNumber=$blockNumber")
            cached
        }
    }

    private suspend fun actualBlockNumber(blockNumber: BigInteger?): BigInteger {
        val currentBlockNumber = getBlockNumber.invoke()
        val adjustedBlockNumber = blockNumber ?: (currentBlockNumber + BigInteger.ONE)
        return adjustedBlockNumber.takeIf { it >= BigInteger.ZERO } ?: (currentBlockNumber + blockNumber!!)
    }
}
