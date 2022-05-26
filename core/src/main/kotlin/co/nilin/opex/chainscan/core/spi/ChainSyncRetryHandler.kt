package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.model.ChainSyncSchedule

interface ChainSyncRetryHandler {
    suspend fun handleNextTry(syncSchedule: ChainSyncSchedule, records: ChainSyncRecord, sentBlock: Long)
}
