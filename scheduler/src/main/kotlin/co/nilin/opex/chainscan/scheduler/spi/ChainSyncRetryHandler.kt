package co.nilin.opex.chainscan.scheduler.spi

import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule

interface ChainSyncRetryHandler {
    suspend fun handleNextTry(syncSchedule: ChainSyncSchedule, records: ChainSyncRecord, sentBlock: Long)
}