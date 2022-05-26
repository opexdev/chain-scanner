package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.spi.ChainSyncRecordHandler
import co.nilin.opex.chainscan.core.spi.GetBlockNumber
import co.nilin.opex.chainscan.scannerdb.model.ChainSyncRecordModel
import co.nilin.opex.chainscan.scannerdb.repository.ChainSyncRecordRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

@Component
class ChainSyncRecordHandlerImpl(
    private val chainSyncRecordRepository: ChainSyncRecordRepository,
    private val getBlockNumber: GetBlockNumber
) :
    ChainSyncRecordHandler {
    override suspend fun lastSyncRecord(): ChainSyncRecord? {
        return chainSyncRecordRepository.findAll().awaitFirstOrNull()?.let {
            ChainSyncRecord(it.syncTime, it.blockNumber, it.id)
        }
    }

    override suspend fun lastSyncedBlockedNumber(): BigInteger {
        val chainSyncRecordDao = chainSyncRecordRepository.findAll().awaitFirstOrNull()
        return chainSyncRecordDao?.blockNumber ?: getBlockNumber.invoke()
    }

    @Transactional
    override suspend fun saveSyncRecord(syncRecord: ChainSyncRecord) {
        val currentRecord = chainSyncRecordRepository.findAll().awaitFirstOrNull()
        val chainSyncRecordDao = ChainSyncRecordModel(
            syncRecord.syncTime,
            syncRecord.blockNumber,
            syncRecord.id ?: currentRecord?.id
        )
        chainSyncRecordRepository.save(chainSyncRecordDao).awaitFirst()
    }
}
