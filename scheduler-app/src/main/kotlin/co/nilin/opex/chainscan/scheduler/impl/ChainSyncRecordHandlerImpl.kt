package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.api.ChainSyncRecordHandler
import co.nilin.opex.chainscan.scheduler.dto.toModel
import co.nilin.opex.chainscan.scheduler.dto.toPlainObject
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.repository.ChainSyncRecordRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class ChainSyncRecordHandlerImpl(
    private val chainSyncRecordRepository: ChainSyncRecordRepository
) : ChainSyncRecordHandler {
    override suspend fun lastSyncRecord(chainName: String): ChainSyncRecord? {
        return chainSyncRecordRepository.findByChain(chainName).awaitFirstOrNull()?.toPlainObject()
    }

    override suspend fun lastSyncedBlockedNumber(chainName: String): BigInteger? {
        val chainSyncRecordDao = chainSyncRecordRepository.findByChain(chainName).awaitFirstOrNull()
        return chainSyncRecordDao?.blockNumber
    }

    override suspend fun saveSyncRecord(syncRecord: ChainSyncRecord) {
        val currentRecord = chainSyncRecordRepository.findAll().awaitFirstOrNull()
        val chainSyncRecordDao = syncRecord.toModel().copy(id = syncRecord.id ?: currentRecord?.id)
        chainSyncRecordRepository.save(chainSyncRecordDao).awaitFirst()
    }
}
