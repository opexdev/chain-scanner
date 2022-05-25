package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.bcgateway.core.spi.ChainSyncRecordHandler
import co.nilin.opex.chainscan.scheduler.spi.ChainEndpointHandler
import co.nilin.opex.chainscan.scheduler.spi.ChainEndpointProxy
import co.nilin.opex.chainscan.scheduler.spi.ChainSyncRetryHandler
import co.nilin.opex.chainscan.scheduler.spi.ChainSyncSchedulerHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.time.LocalDateTime
import kotlin.coroutines.coroutineContext

open class ChainSyncService(
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainEndpointHandler: ChainEndpointHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val operator: TransactionalOperator,
) {
    private val logger = LoggerFactory.getLogger(ChainSyncService::class.java)

    suspend fun startSyncWithChain() = withContext(coroutineContext) {
        val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
        schedules.map { syncSchedule ->
            launch {
                val syncHandler = chainEndpointHandler.findChainEndpointProxy(syncSchedule.chainName)
                val lastSync = chainSyncRecordHandler.loadLastSuccessRecord(syncSchedule.chainName)

                logger.info("chain syncing for: ${syncSchedule.chainName} - block: ${lastSync?.latestBlock}")
                val syncResult =
                    syncHandler.syncTransfers(
                        ChainEndpointProxy.DepositFilter(
                            lastSync?.latestBlock, null, emptyList()
                        )
                    )

                if (syncResult.success)
                    logger.info("request successful - synced ${syncSchedule.chainName} until ${syncResult.latestBlock}")
                else
                    logger.info("request failed - ${syncResult.error}")

                operator.executeAndAwait {
                    chainSyncRecordHandler.saveSyncRecord(syncResult)
                    chainSyncSchedulerHandler.prepareScheduleForNextTry(syncSchedule, syncResult.success)
                    chainSyncRetryHandler.handleNextTry(syncSchedule, syncResult, lastSync?.latestBlock ?: 0)
                }
            }
        }
    }
}
