package co.nilin.opex.chainscanner.scheduler.core.spi

import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule

interface ScheduleTask {
    suspend fun execute(sch: ChainSyncSchedule, chainScanner: ChainScanner)
}
