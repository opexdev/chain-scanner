package co.nilin.opex.bcgateway.core.spi

import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord

interface ChainSyncRecordHandler {
    suspend fun loadLastSuccessRecord(chainName: String): ChainSyncRecord?
    suspend fun saveSyncRecord(syncRecord: ChainSyncRecord)
}
