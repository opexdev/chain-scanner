package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.model.ChainSyncRecordModel
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.repository.ChainSyncRecordRepository
import co.nilin.opex.chainscan.scheduler.api.ChainSyncRecordHandler
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

@Component
class ChainSyncRecordHandlerImpl(
    private val chainSyncRecordRepository: ChainSyncRecordRepository
) : ChainSyncRecordHandler {
    override suspend fun lastSyncRecord(): ChainSyncRecord? {
        return chainSyncRecordRepository.findAll().awaitFirstOrNull()?.let {
            ChainSyncRecord(it.chain, it.syncTime, it.blockNumber, it.id)
        }
    }

    override suspend fun lastSyncedBlockedNumber(): BigInteger? {
        val chainSyncRecordDao = chainSyncRecordRepository.findAll().awaitFirstOrNull()
        return chainSyncRecordDao?.blockNumber
    }

    @Transactional
    override suspend fun saveSyncRecord(syncRecord: ChainSyncRecord) {
        val currentRecord = chainSyncRecordRepository.findAll().awaitFirstOrNull()
        val chainSyncRecordDao = ChainSyncRecordModel(
            syncRecord.chain,
            syncRecord.syncTime,
            syncRecord.blockNumber,
            syncRecord.id ?: currentRecord?.id
        )
        chainSyncRecordRepository.save(chainSyncRecordDao).awaitFirst()
    }
}
