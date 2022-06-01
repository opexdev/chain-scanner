package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule

interface ScheduledJob {
    suspend fun execute(sch: ChainSyncSchedule)
}