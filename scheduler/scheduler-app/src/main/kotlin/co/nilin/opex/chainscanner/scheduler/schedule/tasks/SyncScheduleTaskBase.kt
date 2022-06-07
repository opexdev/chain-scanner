package co.nilin.opex.chainscanner.scheduler.schedule.tasks

import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScheduleTask
import co.nilin.opex.chainscanner.scheduler.exceptions.RateLimitException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

abstract class SyncScheduleTaskBase(
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler
) : ScheduleTask {
    abstract override suspend fun execute(sch: ChainSyncSchedule)

    protected suspend fun ChainSyncSchedule.enqueueNextSchedule(nextTimeDiff: Long) {
        val retryTime = LocalDateTime.now().plus(nextTimeDiff, ChronoUnit.SECONDS)
        chainSyncSchedulerHandler.save(copy(executeTime = retryTime))
    }

    protected suspend fun rethrowScheduleExceptions(
        e: Throwable,
        sch: ChainSyncSchedule,
        chainScanner: ChainScanner
    ) = when (e) {
        is RateLimitException -> sch.enqueueNextSchedule(chainScanner.delayOnRateLimit.toLong())
        else -> throw e
    }
}
