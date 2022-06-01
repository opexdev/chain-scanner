package co.nilin.opex.chainscan.scheduler.core.spi

import co.nilin.opex.chainscan.scheduler.core.po.ChainSyncSchedule

interface ScheduledJob {
    suspend fun execute(sch: ChainSyncSchedule)
}
