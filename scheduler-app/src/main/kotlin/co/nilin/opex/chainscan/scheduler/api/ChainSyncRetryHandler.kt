package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule

interface ChainSyncRetryHandler {
    suspend fun handleNextTry(syncSchedule: ChainSyncSchedule, sentBlock: Long) {}
}
