package co.nilin.opex.chainscan.scheduler.jobs

import co.nilin.opex.chainscan.scheduler.api.*
import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

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
                        val response = scannerProxy.getTransfers(chain.url, retry.blockNumber)
                        webhookCaller.callWebhook("$onSyncWebhookUrl/${chain.chainName}", response)
                        chainSyncRecordHandler.lastSyncRecord(sch.chainName)
                    }.onFailure {
                        val retries = retry.retries + 1
                        chainSyncRetryHandler.save(retry.copy(retries = retries, giveUp = retries >= 5))
                    }.onSuccess {
                        val retries = retry.retries + 1
                        chainSyncRetryHandler.save(retry.copy(retries = retries, synced = true))
                    }
                }
            }
        }
    }
}
