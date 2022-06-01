package co.nilin.opex.chainscan.scheduler.core.spi

import co.nilin.opex.chainscan.scheduler.core.po.ChainSyncRecord
import java.math.BigInteger

interface ChainSyncRecordHandler {
    suspend fun lastSyncRecord(chainName: String): ChainSyncRecord?
    suspend fun lastSyncedBlockedNumber(chainName: String): BigInteger?
    suspend fun saveSyncRecord(syncRecord: ChainSyncRecord)
}
