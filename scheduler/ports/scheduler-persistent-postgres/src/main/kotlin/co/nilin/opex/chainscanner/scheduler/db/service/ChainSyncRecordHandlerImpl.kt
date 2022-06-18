package co.nilin.opex.chainscanner.scheduler.db.service

import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncRecordHandler
import co.nilin.opex.chainscanner.scheduler.db.dto.toModel
import co.nilin.opex.chainscanner.scheduler.db.dto.toPlainObject
import co.nilin.opex.chainscanner.scheduler.db.repository.ChainSyncRecordRepository
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRecord
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
