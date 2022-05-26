package co.nilin.opex.chainscan.scannerdb.service

import co.nilin.opex.chainscan.core.spi.ChainEndpointHandler
import co.nilin.opex.chainscan.core.spi.ChainEndpointProxy
import co.nilin.opex.chainscan.core.spi.ChainSyncRecordHandler
import co.nilin.opex.chainscan.core.spi.TokenAddressHandler
import org.slf4j.LoggerFactory
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

class ChainSyncService(
    private val chainEndpointHandler: ChainEndpointHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val tokenAddressHandler: TokenAddressHandler,
    private val operator: TransactionalOperator,
) {
    private val logger = LoggerFactory.getLogger(ChainSyncService::class.java)

    suspend fun startSyncWithChain() {
        val chainName = ""
        val syncHandler = chainEndpointHandler.findChainEndpointProxy()
        val lastSync = chainSyncRecordHandler.loadLastSuccessRecord()
        val tokens = tokenAddressHandler.findTokenAddresses().map { impl -> impl.address }.toList()

        logger.info("chain syncing for: $chainName - block: ${lastSync?.blockNumber}")
        val syncResult =
            syncHandler.syncTransfers(ChainEndpointProxy.DepositFilter(lastSync?.blockNumber, null, tokens))

//        if (syncResult.success)
//            logger.info("request successful - synced $chainName until ${syncResult.blockNumber}")
//        else
//            logger.info("request failed - ${syncResult.error}")

        operator.executeAndAwait {
            chainSyncRecordHandler.saveSyncRecord(syncResult)
        }
    }
}
