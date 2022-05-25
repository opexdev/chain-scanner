package co.nilin.opex.chainscan.scheduler.spi

import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord

interface ChainSyncRecordHandler {
    suspend fun loadLastSuccessRecord(chainName: String): ChainSyncRecord?
    suspend fun saveSyncRecord(syncRecord: ChainSyncRecord)
}
