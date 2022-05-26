package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.model.TransferResult
import co.nilin.opex.chainscan.core.spi.ChainSyncRecordHandler
import co.nilin.opex.chainscan.scannerdb.model.ChainSyncRecordModel
import co.nilin.opex.chainscan.scannerdb.repository.ChainSyncRecordRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

@Component
class ChainSyncRecordHandlerImpl(private val chainSyncRecordRepository: ChainSyncRecordRepository) :
    ChainSyncRecordHandler {
    override suspend fun lastSyncRecord(): ChainSyncRecord? {
        val chainSyncRecordDao = chainSyncRecordRepository.findAll().awaitFirstOrNull()
        return if (chainSyncRecordDao != null) {
            ChainSyncRecord(
                chainSyncRecordDao.syncTime,
                chainSyncRecordDao.endpointUrl,
                chainSyncRecordDao.blockNumber,
                chainSyncRecordDao.id
            )
        } else {
            null
        }
    }

    override suspend fun lastSyncedBlockedNumber(): BigInteger {
        val chainSyncRecordDao = chainSyncRecordRepository.findAll().awaitFirstOrNull()
        return chainSyncRecordDao?.blockNumber ?: TODO("Fetch chain's last block number")
    }

    @Transactional
    override suspend fun saveSyncRecord(syncRecord: TransferResult) {
        val currentRecord = chainSyncRecordRepository.findAll().awaitFirstOrNull()
        val chainSyncRecordDao =
            ChainSyncRecordModel(
                syncRecord.chainSyncRecord.id!!,
                syncRecord.chainSyncRecord.syncTime,
                syncRecord.chainSyncRecord.endpointUrl,
                syncRecord.chainSyncRecord.blockNumber
            )

        if (currentRecord != null)
            chainSyncRecordRepository.save(chainSyncRecordDao).awaitFirst()
        else
            chainSyncRecordRepository.insert(
                syncRecord.chainSyncRecord.syncTime,
                syncRecord.chainSyncRecord.endpointUrl,
                syncRecord.chainSyncRecord.blockNumber
            )
                .awaitFirstOrNull()
    }
}
