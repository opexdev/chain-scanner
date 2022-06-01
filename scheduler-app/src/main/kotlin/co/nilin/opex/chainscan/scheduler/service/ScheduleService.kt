package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.ChainSyncSchedulerHandler
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
    private val mainSyncScope: CoroutineScope,
    private val retrySyncScope: CoroutineScope,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler
) {
    private val logger: Logger by LoggerDelegate()

    @Scheduled(fixedDelay = 1000)
    fun syncLatestTransfers() {
        logger.trace("Run `syncLatestTransfers` job")
        if (mainSyncScope.isCompleted()) {
            mainSyncScope.launch {
                val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
                supervisorScope {
                    schedules.forEach { sch ->
                        launch {
                            withTimeoutOrNull(sch.timeout) { mainSyncJob.execute(sch) }
                        }
                    }
                }
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    fun retryFailedSyncsJob() {
        logger.trace("Run `retryFailedSyncsJob` job")
        if (retrySyncScope.isCompleted()) {
            retrySyncScope.launch {
                supervisorScope {
                    val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
                    schedules.forEach { sch ->
                        launch {
                            withTimeoutOrNull(sch.timeout) { retrySyncJob.execute(sch) }
                        }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.isCompleted() = coroutineContext.job.children.all { it.isCompleted }
}
