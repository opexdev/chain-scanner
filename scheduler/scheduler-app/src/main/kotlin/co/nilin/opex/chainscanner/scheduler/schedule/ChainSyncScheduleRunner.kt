package co.nilin.opex.chainscanner.scheduler.schedule

import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScheduleTask
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime

abstract class ChainSyncScheduleRunner(
    private val name: String,
    private val scheduleTask: ScheduleTask,
    private val scope: CoroutineScope,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler
) {
    private val logger: Logger by LoggerDelegate()

    @Scheduled(fixedDelay = 1000, initialDelay = 60000)
    fun runSchedules() {
        logger.debug("Executing schedule: $name")
        if (!scope.isCompleted()) return
        scope.launch {
            val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
            supervisorScope {
                schedules.forEach { sch ->
                    launch {
                        runCatching {
                            withTimeout(sch.timeout * 1000) { scheduleTask.execute(sch) }
                        }.onFailure { e ->
                            if (e is TimeoutCancellationException)
                                logger.error("Timeout on chain: ${sch.chainName} schedule: $name")
                        }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.isCompleted() = coroutineContext.job.children.all { it.isCompleted }
}
