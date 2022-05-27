package co.nilin.opex.chainscan.scheduler.service

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service

@Service
class ScheduleServiceTest {
    private var scheduleService: ScheduleService = ScheduleService()

    @Test
    fun given(): Unit = runBlocking {
        scheduleService.start()
    }
}
