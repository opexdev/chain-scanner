package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.model.Deposit
import co.nilin.opex.chainscan.core.model.Endpoint
import co.nilin.opex.chainscan.core.spi.ChainSyncRecordHandler
import co.nilin.opex.chainscan.scannerdb.model.ChainSyncRecordModel
import co.nilin.opex.chainscan.scannerdb.repository.ChainSyncRecordRepository
import co.nilin.opex.chainscan.scannerdb.repository.DepositRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ChainSyncRecordHandlerImpl(
    private val chainSyncRecordRepository: ChainSyncRecordRepository,
    private val depositRepository: DepositRepository
) : ChainSyncRecordHandler {
    override suspend fun loadLastSuccessRecord(chainName: String): ChainSyncRecord? {
        val chainSyncRecordDao = chainSyncRecordRepository.findByChain(chainName).awaitSingleOrNull()
        return if (chainSyncRecordDao != null) {
            val deposits = depositRepository.findByChainWhereNotSynced(chainName).map {
                Deposit(it.id, it.hash, it.depositor, it.depositorMemo, it.amount, it.chain, it.token, it.tokenAddress)
            }

            ChainSyncRecord(
                chainSyncRecordDao.chain,
                chainSyncRecordDao.time,
                Endpoint(chainSyncRecordDao.endpointUrl),
                if (chainSyncRecordDao.latestBlock == null) 0 else chainSyncRecordDao.latestBlock + 1,
                chainSyncRecordDao.success,
                chainSyncRecordDao.error,
                deposits.toList()
            )
        } else {
            null
        }
    }

    @Transactional
    override suspend fun saveSyncRecord(syncRecord: ChainSyncRecord) {
        val currentRecord = chainSyncRecordRepository.findByChain(syncRecord.chainName).awaitSingleOrNull()
        val chainSyncRecordDao =
            ChainSyncRecordModel(
                syncRecord.chainName,
                syncRecord.time,
                syncRecord.endpoint.url,
                syncRecord.latestBlock ?: currentRecord?.latestBlock,
                syncRecord.success,
                syncRecord.error
            )

        if (currentRecord != null)
            chainSyncRecordRepository.save(chainSyncRecordDao).awaitFirst()
        else
            chainSyncRecordRepository.insert(
                syncRecord.chainName,
                syncRecord.time,
                syncRecord.endpoint.url,
                syncRecord.latestBlock ?: currentRecord?.latestBlock,
                syncRecord.success,
                syncRecord.error
            ).awaitFirstOrNull()
    }
}
