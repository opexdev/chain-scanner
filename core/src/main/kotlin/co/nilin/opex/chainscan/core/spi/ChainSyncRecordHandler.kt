package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import java.math.BigInteger

interface ChainSyncRecordHandler {
    suspend fun lastSyncRecord(consumerId: Long): ChainSyncRecord?
    suspend fun lastSyncedBlockedNumber(consumerId: Long): BigInteger
    suspend fun saveSyncRecord(syncRecord: ChainSyncRecord)
}
