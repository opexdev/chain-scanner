package co.nilin.opex.chainscan.scheduler.service

import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduleService() {
    @Scheduled(fixedDelay = 1000)
    fun start(): Nothing = runBlocking {
        TODO("Get all chains scheduled for scan")
        TODO("Request for transfer list")
        TODO("Call webhook")
        TODO("Clear transfer cache on webhook call success")
    }
}
