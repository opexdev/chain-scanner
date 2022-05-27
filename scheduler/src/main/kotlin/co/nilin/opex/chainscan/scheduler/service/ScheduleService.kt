package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.ChainSyncSchedulerHandler
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private const val TIMESTAMP = 1653659069L

@Service
class ScheduleService(
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler
) {
    @Scheduled(fixedDelay = 1000)
    fun start(): Nothing = runBlocking {
        val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
        TODO("Request for transfer list")
        TODO("Call webhook")
        TODO("Clear transfer cache on webhook call success")
    }
}
