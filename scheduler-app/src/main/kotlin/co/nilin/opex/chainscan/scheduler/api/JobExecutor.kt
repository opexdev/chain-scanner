package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule

interface JobExecutor {
    suspend fun execute(sch: ChainSyncSchedule)
}