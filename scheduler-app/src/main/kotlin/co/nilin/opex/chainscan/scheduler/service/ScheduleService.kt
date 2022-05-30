package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.*
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRetry
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class ScheduleService(
    private val scannerProxy: ScannerProxy,
    private val chainSyncRecordHandler: ChainSyncRecordHandler,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val chainSyncRetryHandler: ChainSyncRetryHandler,
    private val webhookCaller: WebhookCaller,
    private val chainScannerHandler: ChainScannerHandler,
    @Value("\${app.on-sync-webhook-url}") private val onSyncWebhookUrl: String,
) {
    private val logger: Logger by LoggerDelegate()

    @Scheduled(fixedDelay = 1000)
    fun start(): Unit = runBlocking {
        logger.info("Running start() schedule")
        val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
        schedules.forEach { sch ->
            val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
            runCatching {
                val response = scannerProxy.getTransfers(chain.url)
                webhookCaller.callWebhook(onSyncWebhookUrl, response.transfers)
                val record = chainSyncRecordHandler.lastSyncRecord(sch.chainName)
                chainSyncRecordHandler.saveSyncRecord(
                    record?.copy(syncTime = LocalDateTime.now(), blockNumber = response.toBlockNumber)
                        ?: ChainSyncRecord(sch.chainName, LocalDateTime.now(), response.toBlockNumber)
                )
            }.onFailure {
                chainSyncRetryHandler.save(ChainSyncRetry(sch.chainName, BigInteger.ZERO, BigInteger.ZERO))
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    fun startRetry(): Unit = runBlocking {
        logger.info("Running startRetry() schedule")
        val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
        schedules.forEach { sch ->
            val chain = chainScannerHandler.getScannersByName(sch.chainName).first()
            val chainSyncRetries = chainSyncRetryHandler.findAllActive(sch.chainName)
            chainSyncRetries.forEach { retry ->
                runCatching {
                    val response = scannerProxy.getTransfers(chain.url)
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
