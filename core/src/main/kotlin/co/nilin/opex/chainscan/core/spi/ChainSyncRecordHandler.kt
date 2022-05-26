package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.model.DepositResult
import java.math.BigInteger

interface ChainSyncRecordHandler {
    suspend fun loadLastSuccessRecord(): ChainSyncRecord?
    suspend fun lastSyncedBlockedNumber(): BigInteger
    suspend fun saveSyncRecord(syncRecord: DepositResult)
}
