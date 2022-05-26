package co.nilin.opex.chainscan.scannerdb.service

import co.nilin.opex.chainscan.core.spi.ChainEndpointHandler
import co.nilin.opex.chainscan.core.spi.ChainEndpointProxy
import co.nilin.opex.chainscan.core.spi.ChainSyncRecordHandler
import co.nilin.opex.chainscan.core.spi.TokenAddressHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

class ChainSyncService(
    @Value("\$chain-name") private val chainName: String,
    private val chainEndpointHandler: ChainEndpointHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val tokenAddressHandler: TokenAddressHandler,
    private val operator: TransactionalOperator,
) {
    private val logger = LoggerFactory.getLogger(ChainSyncService::class.java)

    suspend fun startSyncWithChain() {
        val chainEndpointProxy = chainEndpointHandler.findChainEndpointProxy()
        val lastSync = chainSyncRecordHandler.lastSyncedBlockedNumber()
        val tokens = tokenAddressHandler.findTokenAddresses().map { impl -> impl.address }.toList()

        logger.info("chain syncing for: $chainName - block: $lastSync")
        val syncResult = runCatching {
            chainEndpointProxy.syncTransfers(ChainEndpointProxy.DepositFilter(lastSync, null, tokens))
        }.onFailure {
            logger.info("request failed - ${it.message}")
        }.onSuccess {
            logger.info("request successful - synced $chainName until ${it.chainSyncRecord.blockNumber}")
        }.getOrThrow()

        operator.executeAndAwait {
            chainSyncRecordHandler.saveSyncRecord(syncResult)
        }
    }
}
