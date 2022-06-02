package co.nilin.opex.chainscanner.scheduler.service

import co.nilin.opex.chainscanner.scheduler.core.spi.*
import co.nilin.opex.chainscanner.scheduler.coroutines.Dispatchers
import co.nilin.opex.chainscanner.scheduler.jobs.RetryFailedSyncsScheduledJob
import co.nilin.opex.chainscanner.scheduler.jobs.SyncLatestTransfersScheduledJob
import co.nilin.opex.chainscanner.scheduler.sample.VALID
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScheduleServiceTest {
    private val chainSyncRecordHandler: ChainSyncRecordHandler = mockk()
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler = mockk()
    private val chainSyncRetryHandler: ChainSyncRetryHandler = mockk()
    private val scannerProxy: ScannerProxy = mockk()
    private val chainScannerHandler: ChainScannerHandler = mockk()
    private val webhookCaller: WebhookCaller = mockk()
    private val onSyncWebhookUrl: String = "http://bc-gateway"
    private val mainSyncJob: SyncLatestTransfersScheduledJob = mockk()
    private val retrySyncJob: RetryFailedSyncsScheduledJob = mockk()
    private val mainSyncScope: CoroutineScope = CoroutineScope(Dispatchers.SCHEDULER)
    private val retrySyncScope: CoroutineScope = CoroutineScope(Dispatchers.SCHEDULER)
    private val scheduleService: ScheduleService = ScheduleService(
        mainSyncJob,
        retrySyncJob,
        mainSyncScope,
        retrySyncScope,
        chainSyncSchedulerHandler
    )

    @Test
    fun givenSchedule_whenRunSync_thenSuccess(): Unit = runBlocking {
        mockkStatic(LocalDateTime::class)
        every { LocalDateTime.now() } returns VALID.CURRENT_LOCAL_DATE_TIME
        coEvery {
            scannerProxy.getTransfers(VALID.SCHEDULE.chainName)
        } returns emptyList()
        coEvery {
            webhookCaller.callWebhook(onSyncWebhookUrl, listOf(VALID.TRANSFER))
        } returns Unit
        coEvery {
            chainSyncRecordHandler.lastSyncRecord(VALID.SCHEDULE.chainName)
        } returns null
        coEvery {
            chainSyncRecordHandler.saveSyncRecord(VALID.CHAIN_SYNC_RECORD)
        } returns Unit
        coEvery {
            chainSyncSchedulerHandler.fetchActiveSchedules(VALID.CURRENT_LOCAL_DATE_TIME)
        } returns listOf(VALID.SCHEDULE)
        coEvery {
            chainScannerHandler.getScannersByName(VALID.BITCOIN)
        } returns listOf(VALID.CHAIN_SCANNER)
        coEvery {
            chainSyncRetryHandler.save(VALID.NEW_CHAIN_SYNC_RETRY)
        } returns Unit
        coEvery {
            scannerProxy.getTransfers(VALID.CHAIN_SCANNER.url)
        } returns listOf(VALID.TRANSFER)

        scheduleService.syncLatestTransfers()

        coVerify(exactly = 1) {
            webhookCaller.callWebhook(onSyncWebhookUrl, listOf(VALID.TRANSFER))
        }
    }
}
