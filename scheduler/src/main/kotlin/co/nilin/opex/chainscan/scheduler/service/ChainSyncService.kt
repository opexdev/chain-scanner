package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.spi.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.time.LocalDateTime
import kotlin.coroutines.coroutineContext

open class ChainSyncService(
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainEndpointHandler: ChainEndpointHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val tokenAddressHandler: TokenAddressHandler,
    private val webhookCaller: WebhookCaller,
    @Value("\${webhook}") private val webhookBaseUrl: String,
    private val operator: TransactionalOperator,
) {
    private val logger = LoggerFactory.getLogger(ChainSyncService::class.java)

    suspend fun startSyncWithChain() = withContext(coroutineContext) {
        val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
        schedules.map { syncSchedule ->
            launch {
                val syncHandler = chainEndpointHandler.findChainEndpointProxy(syncSchedule.chainName)
                val lastSync = chainSyncRecordHandler.loadLastSuccessRecord(syncSchedule.chainName)
                val tokens = tokenAddressHandler.findTokenAddresses(syncSchedule.chainName)
                    .map { impl -> impl.address }
                    .toList()

                logger.info("chain syncing for: ${syncSchedule.chainName} - block: ${lastSync?.latestBlock}")
                val syncResult =
                    syncHandler.syncTransfers(
                        ChainEndpointProxy.DepositFilter(lastSync?.latestBlock, null, tokens)
                    )

                if (syncResult.success)
                    logger.info("request successful - synced ${syncSchedule.chainName} until ${syncResult.latestBlock}")
                else
                    logger.info("request failed - ${syncResult.error}")

                operator.executeAndAwait {
                    chainSyncRecordHandler.saveSyncRecord(syncResult)
                    webhookCaller.callWebhook(webhookBaseUrl, syncResult)
                    chainSyncSchedulerHandler.prepareScheduleForNextTry(syncSchedule, syncResult.success)
                    chainSyncRetryHandler.handleNextTry(syncSchedule, syncResult, lastSync?.latestBlock ?: 0)
                }
            }
        }
    }
}
