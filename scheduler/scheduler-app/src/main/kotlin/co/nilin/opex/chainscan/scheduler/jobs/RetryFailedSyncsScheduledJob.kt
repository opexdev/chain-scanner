package co.nilin.opex.chainscan.scheduler.jobs

import co.nilin.opex.chainscan.scheduler.core.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscan.scheduler.core.spi.*
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RetryFailedSyncsScheduledJob(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller,
    @Value("\${app.on-sync-webhook-url}") private val onSyncWebhookUrl: String
) : ScheduledJob {
    private val logger: Logger by LoggerDelegate()

    override suspend fun execute(sch: ChainSyncSchedule) {
        val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
        val chainSyncRetries = chainSyncRetryHandler.findAllActive(sch.chainName)
        supervisorScope {
            chainSyncRetries.forEach { retry ->
                launch {
                    logger.trace("Retry block sync on: ${retry.blockNumber}")
                    runCatching {
                        scannerProxy.getTransfers(chain.url, retry.blockNumber)
                    }.onFailure {
                        val retries = retry.retries + 1
                        chainSyncRetryHandler.save(retry.copy(retries = retries, giveUp = retries >= sch.maxRetries))
                    }.mapCatching { response ->
                        webhookCaller.callWebhook("$onSyncWebhookUrl/${chain.chainName}", response)
                    }.onSuccess {
                        val lastSyncRecord = chainSyncRecordHandler.lastSyncRecord(sch.chainName) ?: ChainSyncRecord(
                            sch.chainName,
                            LocalDateTime.now(),
                            retry.blockNumber
                        )
                        chainSyncRecordHandler.saveSyncRecord(
                            lastSyncRecord.copy(
                                syncTime = LocalDateTime.now(),
                                blockNumber = retry.blockNumber
                            )
                        )
                        val retries = retry.retries + 1
                        chainSyncRetryHandler.save(retry.copy(retries = retries, synced = true))
                    }
                }
            }
        }
    }
}
