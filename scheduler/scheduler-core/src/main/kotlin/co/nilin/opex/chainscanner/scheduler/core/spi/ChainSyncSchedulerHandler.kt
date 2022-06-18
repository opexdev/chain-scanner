package co.nilin.opex.chainscanner.scheduler.core.spi

import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import java.time.LocalDateTime

interface ChainSyncSchedulerHandler {
    suspend fun fetchActiveSchedules(dateTime: LocalDateTime): List<ChainSyncSchedule>
    suspend fun save(syncSchedule: ChainSyncSchedule)
    suspend fun scheduleChain(chain: String, delaySeconds: Long, errorDelaySeconds: Long)
    suspend fun findByChain(chainName: String): ChainSyncSchedule?
}
