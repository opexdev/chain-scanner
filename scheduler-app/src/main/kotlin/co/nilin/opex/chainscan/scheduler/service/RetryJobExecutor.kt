package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.*
import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RetryJobExecutor(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller,
    @Value("\${app.on-sync-webhook-url}") private val onSyncWebhookUrl: String
) : JobExecutor {
    override suspend fun execute(sch: ChainSyncSchedule) {
        val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
        val chainSyncRetries = chainSyncRetryHandler.findAllActive(sch.chainName)
        supervisorScope {
            chainSyncRetries.forEach { retry ->
                launch {
                    runCatching {
                        val response = scannerProxy.getTransfers(chain.url, retry.blockNumber)
                        webhookCaller.callWebhook(onSyncWebhookUrl, response.transfers)
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
