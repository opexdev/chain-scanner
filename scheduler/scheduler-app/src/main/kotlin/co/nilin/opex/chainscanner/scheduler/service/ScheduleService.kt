package co.nilin.opex.chainscanner.scheduler.service

import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.jobs.RetryFailedSyncsScheduledJob
import co.nilin.opex.chainscanner.scheduler.jobs.SyncLatestTransfersScheduledJob
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
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
                            runCatching {
                                withTimeout(sch.timeout * 1000) { mainSyncJob.execute(sch) }
                            }.onFailure {
                                if (it is TimeoutCancellationException)
                                    logger.error("Timeout reached on chain: ${sch.chainName}")
                            }
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
                            runCatching {
                                withTimeout(sch.timeout * 1000) { retrySyncJob.execute(sch) }
                            }.onFailure {
                                if (it is TimeoutCancellationException)
                                    logger.error("Timeout reached when retrying chain: ${sch.chainName}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.isCompleted() = coroutineContext.job.children.all { it.isCompleted }
}
