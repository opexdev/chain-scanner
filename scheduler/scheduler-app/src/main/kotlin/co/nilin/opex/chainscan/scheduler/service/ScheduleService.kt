package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscan.scheduler.jobs.RetryFailedSyncsScheduledJob
import co.nilin.opex.chainscan.scheduler.jobs.SyncLatestTransfersScheduledJob
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScheduleService(
    private val mainSyncJob: SyncLatestTransfersScheduledJob,
    private val retrySyncJob: RetryFailedSyncsScheduledJob,
    private val syncLatestTransfersScope: CoroutineScope,
    private val retryFailedSyncsScope: CoroutineScope,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler
) {
    private val logger: Logger by LoggerDelegate()

    @Scheduled(fixedDelay = 1000)
    fun syncLatestTransfers() {
        if (syncLatestTransfersScope.isCompleted()) {
            syncLatestTransfersScope.launch {
                val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
                supervisorScope {
                    schedules.forEach { sch ->
                        launch {
                            withTimeoutOrNull(sch.timeout * 1000) { mainSyncJob.execute(sch) }
                        }
                    }
                }
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    fun retryFailedSyncsJob() {
        if (retryFailedSyncsScope.isCompleted()) {
            retryFailedSyncsScope.launch {
                supervisorScope {
                    val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
                    schedules.forEach { sch ->
                        launch {
                            withTimeoutOrNull(sch.timeout * 1000) { retrySyncJob.execute(sch) }
                        }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.isCompleted() = coroutineContext.job.children.all { it.isCompleted }
}