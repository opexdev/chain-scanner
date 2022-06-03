package co.nilin.opex.chainscanner.scheduler.schedule

import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScheduleTask
import co.nilin.opex.chainscanner.scheduler.exceptions.ScannerConnectException
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime

abstract class ChainSyncScheduleRunner(
    private val scheduleTask: ScheduleTask,
    private val scope: CoroutineScope,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler
) {
    private val logger: Logger by LoggerDelegate()

    @Scheduled(fixedDelay = 1000, initialDelay = 60000)
    fun runSchedules() {
        if (!scope.isCompleted()) return
        val name = this::class.simpleName
        logger.trace("Executing schedule: $name")
        scope.launch {
            val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
            logger.debug("Schedules count: ${schedules.size}")
            coroutineScope {
                schedules.forEach { sch ->
                    launch {
                        runCatching {
                            withTimeout(sch.timeout * 1000) { scheduleTask.execute(sch) }
                        }.onFailure { e ->
                            if (e is TimeoutCancellationException)
                                logger.error("Timeout on chain: ${sch.chainName} schedule: $name")
                            else if (e is ScannerConnectException) throw e
                        }.onSuccess {
                            logger.debug("Successfully executed schedule: $name chain: ${sch.chainName}")
                        }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.isCompleted() = coroutineContext.job.children.all { it.isCompleted }
}
