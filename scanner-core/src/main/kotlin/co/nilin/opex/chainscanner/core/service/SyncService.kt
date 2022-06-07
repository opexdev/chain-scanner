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
    @Value("\${app.chain.name}") private val chainName: String,
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
        logger.debug("Syncing for: $chainName - Block: $actualBlockNumber")
        val cached = transferCacheHandler.getTransfers(watchedTokens, actualBlockNumber)
        return cached.takeIf { it.isNotEmpty() }.also {
            logger.debug("Loading $chainName transfers from cache on blockNumber: $actualBlockNumber")
        } ?: run {
            logger.debug("Start fetching $chainName transfers on blockNumber: $actualBlockNumber")
            val response = chainService.getTransactions(actualBlockNumber)
            logger.debug("Finished fetching block info on blockNumber: $actualBlockNumber")
            return dataDecoder.decode(response)
                .filter { !it.isTokenTransfer || watchedTokens.contains(it.tokenAddress) }
                .also { transferCacheHandler.saveTransfers(it) }
        }
    }

    suspend fun getTransferByHash(txHash: String): Transfer {
        require(txHash.isNotBlank())
        logger.debug("Fetching for: $chainName - Tx Hash: $txHash")
        val response = chainService.getTransactionByHash(txHash)
        logger.debug("Finished fetching tx info for Tx Hash: $txHash")
        return dataDecoder.decode(response).first()
    }
}
