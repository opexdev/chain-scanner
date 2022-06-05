package co.nilin.opex.chainscanner.scheduler.schedule

import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScheduleTask
import co.nilin.opex.chainscanner.scheduler.coroutines.Dispatchers
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import java.time.Duration
import java.time.LocalDateTime
import javax.annotation.PostConstruct

abstract class ChainSyncScheduleRunner(
    private val scheduleTask: ScheduleTask,
    private val scope: CoroutineScope,
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val errorRate: Int,
    private val errorRatePeriod: Int,
) {
    private val logger: Logger by LoggerDelegate()
    private lateinit var rateLimiterRegistry: RateLimiterRegistry

    @PostConstruct
    fun init() {
        val config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMillis(errorRatePeriod * 1000L))
            .limitForPeriod(errorRate)
            .build()
        rateLimiterRegistry = RateLimiterRegistry.of(config)
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 60000)
    fun runSchedules() {
        scope.ensureActive()
        if (!scope.isCompleted()) return
        scope.launch {
            val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
            logger.debug("Schedules count: ${schedules.size}")
            coroutineScope {
                schedules.forEach { sch ->
                    launch {
                        runCatching {
                            withTimeout(sch.timeout * 1000) { scheduleTask.execute(sch) }
                        }.recoverCatching {
                            rethrowScheduleExceptions(it, sch)
                        }.onFailure {
                            val isRateLimitReached = !rateLimiterRegistry.rateLimiter(sch.chainName).acquirePermission()
                            if (isRateLimitReached) sch.disable()
                        }.onSuccess {
                            logger.debug("Successfully executed chain: ${sch.chainName}")
                        }
                    }
                }
            }
        }
    }

    private fun rethrowScheduleExceptions(e: Throwable, sch: ChainSyncSchedule) {
        if (e is TimeoutCancellationException) logger.error("Schedule timeout on chain: ${sch.chainName}")
        else logger.error("Schedule error on chain: {} message: {}", sch.chainName, e.message)
        throw e
    }

    private fun ChainSyncSchedule.disable() = runBlocking(Dispatchers.SCHEDULE_ACTOR) {
        chainSyncSchedulerHandler.findByChain(chainName)?.takeIf { it.enabled }?.let {
            chainSyncSchedulerHandler.save(it.copy(enabled = false))
            logger.warn("Disabled schedule on chain : $chainName because of reaching error limit")
        }
    }

    private fun CoroutineScope.isCompleted() = coroutineContext.job.children.all { it.isCompleted }
}
