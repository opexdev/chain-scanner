package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.ChainSyncRecordHandler
import co.nilin.opex.chainscan.scheduler.api.ChainSyncSchedulerHandler
import co.nilin.opex.chainscan.scheduler.api.ScannerProxy
import co.nilin.opex.chainscan.scheduler.api.WebhookCaller
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScheduleService(
    private val scannerProxy: ScannerProxy,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val webhookCaller: WebhookCaller,
    @Value("\$webhook") private val webhook: String,
) {
    @Scheduled(fixedDelay = 1000)
    fun start(): Unit = runBlocking {
        val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
        schedules.forEach {
            val response = scannerProxy.getTransfers(it.chainName)
            webhookCaller.callWebhook(webhook, response.transfers)
            val record = chainSyncRecordHandler.lastSyncRecord(it.chainName)
            chainSyncRecordHandler.saveSyncRecord(
                record?.copy(syncTime = LocalDateTime.now(), blockNumber = response.toBlockNumber)
                    ?: ChainSyncRecord(it.chainName, LocalDateTime.now(), response.toBlockNumber)
            )
        }
    }
}
