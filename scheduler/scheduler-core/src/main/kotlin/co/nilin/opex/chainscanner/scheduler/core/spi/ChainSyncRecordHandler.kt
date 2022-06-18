package co.nilin.opex.chainscanner.scheduler.core.spi

import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRecord
import java.math.BigInteger

interface ChainSyncRecordHandler {
    suspend fun lastSyncRecord(chainName: String): ChainSyncRecord?
    suspend fun lastSyncedBlockedNumber(chainName: String): BigInteger?
    suspend fun saveSyncRecord(syncRecord: ChainSyncRecord)
}
