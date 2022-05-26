package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.spi.ChainSyncRecordHandler
import co.nilin.opex.chainscan.scannerdb.model.ChainSyncRecordModel
import co.nilin.opex.chainscan.scannerdb.repository.ChainSyncRecordRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

@Component
class ChainSyncRecordHandlerImpl(private val chainSyncRecordRepository: ChainSyncRecordRepository) :
    ChainSyncRecordHandler {
    override suspend fun lastSyncRecord(consumerId: Long): ChainSyncRecord? {
        return chainSyncRecordRepository.findByConsumerId(consumerId).awaitSingleOrNull()?.let {
            ChainSyncRecord(
                consumerId,
                it.syncTime,
                it.blockNumber,
                it.id
            )
        }
    }

    override suspend fun lastSyncedBlockedNumber(consumerId: Long): BigInteger {
        val chainSyncRecordDao = chainSyncRecordRepository.findAll().awaitFirstOrNull()
        return chainSyncRecordDao?.blockNumber ?: TODO("Fetch chain's last block number")
    }

    @Transactional
    override suspend fun saveSyncRecord(syncRecord: ChainSyncRecord) {
        val currentRecord = chainSyncRecordRepository.findAll().awaitFirstOrNull()
        val chainSyncRecordDao = ChainSyncRecordModel(
            syncRecord.consumerId,
            syncRecord.syncTime,
            syncRecord.blockNumber,
            syncRecord.id ?: currentRecord?.id
        )
        chainSyncRecordRepository.save(chainSyncRecordDao).awaitFirst()
    }
}
