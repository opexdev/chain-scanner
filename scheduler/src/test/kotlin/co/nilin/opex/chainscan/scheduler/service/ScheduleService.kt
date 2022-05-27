package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.ChainSyncSchedulerHandler
import co.nilin.opex.chainscan.scheduler.sample.VALID
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class ScheduleServiceTest {
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler = mockk()
    private val scheduleService: ScheduleService = ScheduleService(chainSyncSchedulerHandler)

    @Test
    fun givenSchedule_whenRunSync_thenSuccess(): Unit = runBlocking {
        coEvery {
            chainSyncSchedulerHandler.fetchActiveSchedules(
                LocalDateTime.ofEpochSecond(VALID.TIMESTAMP, 0, ZoneOffset.UTC)
            )
        } returns listOf(VALID.SCHEDULE)
        mockkStatic(LocalDateTime::class)
        every { LocalDateTime.now() } returns LocalDateTime.ofEpochSecond(VALID.TIMESTAMP, 0, ZoneOffset.UTC)
        scheduleService.start()
    }
}
