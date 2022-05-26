package co.nilin.opex.chainscan.scannerdb.service

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.math.BigInteger
import java.time.LocalDateTime

class ChainSyncService(
    @Value("\$chain-name") private val chainName: String,
    private val chainEndpointHandler: ChainEndpointHandler,
    private val fetchAndConvert: FetchAndConvert,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val tokenAddressHandler: TokenAddressHandler,
    private val transferCacheHandler: TransferCacheHandler,
    private val operator: TransactionalOperator,
) {
    private val logger = LoggerFactory.getLogger(ChainSyncService::class.java)

    suspend fun getTransfers(): List<Transfer> {
        val endpoint = chainEndpointHandler.findAll().first()
        val lastSync = chainSyncRecordHandler.lastSyncedBlockedNumber()
        val startBlock = lastSync + BigInteger.ONE
        val endBlock = startBlock
        val tokens = tokenAddressHandler.findTokenAddresses().map { impl -> impl.address }.toList()
        logger.info("chain syncing for: $chainName - block: $lastSync")
        val cached = transferCacheHandler.getTransfers(tokens)
        val notCachedStartBlock = cached.maxOfOrNull { it.blockNumber }?.plus(BigInteger.ONE) ?: startBlock
        val transfers = fetchAndConvert.fetchAndConvert(endpoint.url, notCachedStartBlock, endBlock, tokens)
        transferCacheHandler.saveTransfers(transfers)
        return cached + transfers
    }

    suspend fun clearCache(consumerId: Long, blockNumber: BigInteger) = operator.executeAndAwait {
        chainSyncRecordHandler.saveSyncRecord(ChainSyncRecord(LocalDateTime.now(), consumerId, blockNumber))
        transferCacheHandler.clearCache(blockNumber)
    }
}
