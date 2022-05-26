package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.ChainSyncRecord

interface ChainSyncRecordHandler {
    suspend fun loadLastSuccessRecord(): ChainSyncRecord?
    suspend fun saveSyncRecord(syncRecord: ChainSyncRecord)
}