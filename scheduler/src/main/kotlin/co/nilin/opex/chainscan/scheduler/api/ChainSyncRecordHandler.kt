package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import java.math.BigInteger

interface ChainSyncRecordHandler {
    suspend fun lastSyncRecord(): ChainSyncRecord?
    suspend fun lastSyncedBlockedNumber(): BigInteger?
    suspend fun saveSyncRecord(syncRecord: ChainSyncRecord)
}
