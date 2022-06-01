package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.*
import co.nilin.opex.chainscan.scheduler.coroutines.Dispatchers
import co.nilin.opex.chainscan.scheduler.po.TransferResult
import co.nilin.opex.chainscan.scheduler.sample.VALID
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service
import java.math.BigInteger
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
    private val mainSyncJob: SyncLatestTransfersJob = mockk()
    private val retrySyncJob: RetryFailedSyncsJob = mockk()
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
        } returns TransferResult(BigInteger.ZERO, emptyList())
        coEvery {
            webhookCaller.callWebhook(onSyncWebhookUrl, VALID.TRANSFER_RESULT.transfers)
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
        } returns VALID.TRANSFER_RESULT

        scheduleService.syncLatestTransfers()

        coVerify(exactly = 1) {
            webhookCaller.callWebhook(onSyncWebhookUrl, VALID.TRANSFER_RESULT.transfers)
        }
    }
}
