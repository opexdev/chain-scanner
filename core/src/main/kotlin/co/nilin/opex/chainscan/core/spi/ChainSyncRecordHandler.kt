package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.model.DepositResult

interface ChainSyncRecordHandler {
    suspend fun loadLastSuccessRecord(): ChainSyncRecord?
    suspend fun saveSyncRecord(syncRecord: DepositResult)
}
