package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule
import java.time.LocalDateTime

interface ChainSyncSchedulerHandler {
    suspend fun fetchActiveSchedules(time: LocalDateTime): List<ChainSyncSchedule>
    suspend fun save(syncSchedule: ChainSyncSchedule)
    suspend fun scheduleChain(chain: String, delaySeconds: Long, errorDelaySeconds: Long)
}
