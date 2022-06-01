package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule

interface SyncJob {
    suspend fun startJob(sch: ChainSyncSchedule)
}