package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import java.math.BigInteger

interface ChainSyncRecordHandler {
    suspend fun lastSyncRecord(): ChainSyncRecord?
    suspend fun lastSyncedBlockedNumber(): BigInteger
    suspend fun saveSyncRecord(syncRecord: ChainSyncRecord)
}
