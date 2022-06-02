package co.nilin.opex.chainscanner.scheduler.core.spi

import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule

interface ScheduledJob {
    suspend fun execute(sch: ChainSyncSchedule)
}
