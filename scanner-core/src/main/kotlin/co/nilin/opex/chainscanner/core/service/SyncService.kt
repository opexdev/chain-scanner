package co.nilin.opex.chainscanner.core.service

import co.nilin.opex.chainscanner.core.model.Transfer
import co.nilin.opex.chainscanner.core.spi.*
import co.nilin.opex.chainscanner.core.utils.LoggerDelegate
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class SyncService<T>(
    @Value("\${app.chain-name}") private val chainName: String,
    private val chainService: ChainService<T>,
    private val dataDecoder: DataDecoder<T>,
    private val watchListHandler: WatchListHandler,
    private val transferCacheHandler: TransferCacheHandler,
    private val addressChecksumer: AddressChecksumer
) {
    private val logger: Logger by LoggerDelegate()

    suspend fun getTransfers(blockNumber: BigInteger? = null): List<Transfer> {
        require(blockNumber?.abs() == blockNumber)
        val actualBlockNumber = blockNumber ?: chainService.getLatestBlock()
        val watchedTokens = watchListHandler.findAll().map { addressChecksumer.makeValid(it.address) }
        logger.info("Syncing for: $chainName - Block: $actualBlockNumber")
        val cached = transferCacheHandler.getTransfers(watchedTokens, actualBlockNumber)
        return if (cached.isEmpty()) {
            logger.info("Start fetching $chainName transfers on blockNumber: $blockNumber")
            val response = chainService.getTransactions(actualBlockNumber)
            logger.info("Finished fetching block info on blockNumber: $blockNumber")
            return dataDecoder.decode(response)
                .filter { !it.isTokenTransfer || watchedTokens.contains(it.tokenAddress) }
                .also { transferCacheHandler.saveTransfers(it) }
        } else {
            logger.info("Loading $chainName transfers from cache on blockNumber: $blockNumber")
            cached
        }
    }
}
