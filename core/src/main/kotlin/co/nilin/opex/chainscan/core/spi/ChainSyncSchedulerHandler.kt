package co.nilin.opex.chainscan.scheduler.spi

import co.nilin.opex.chainscan.core.model.ChainSyncSchedule
import java.time.LocalDateTime

interface ChainSyncSchedulerHandler {
    suspend fun fetchActiveSchedules(time: LocalDateTime): List<ChainSyncSchedule>
    suspend fun prepareScheduleForNextTry(syncSchedule: ChainSyncSchedule, success: Boolean)
    suspend fun scheduleChain(chain: String, delaySeconds: Int, errorDelaySeconds: Int)
}
