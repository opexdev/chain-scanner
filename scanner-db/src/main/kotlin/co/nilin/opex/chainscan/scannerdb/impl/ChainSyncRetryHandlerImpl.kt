package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.model.ChainSyncSchedule
import co.nilin.opex.chainscan.core.spi.ChainSyncRetryHandler
import co.nilin.opex.chainscan.scannerdb.model.ChainSyncRecordModel
import co.nilin.opex.chainscan.scannerdb.model.ChainSyncRetryModel
import co.nilin.opex.chainscan.scannerdb.repository.ChainSyncRecordRepository
import co.nilin.opex.chainscan.scannerdb.repository.ChainSyncRetryRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component

@Component
class ChainSyncRetryHandlerImpl(
    private val chainSyncRetryRepository: ChainSyncRetryRepository,
    private val chainSyncRecordRepository: ChainSyncRecordRepository,
) : ChainSyncRetryHandler {
    private val maxRetry = 5

    override suspend fun handleNextTry(syncSchedule: ChainSyncSchedule, records: ChainSyncRecord, sentBlock: Long) {
        val success = records.success
        val chain = syncSchedule.chainName

        var retry = chainSyncRetryRepository.findByChainAndBlock(chain, sentBlock).awaitFirstOrNull()
        if (success) {
            if (retry != null) {
                retry.apply {
                    retries += 1
                    synced = true
                }
                chainSyncRetryRepository.save(retry).awaitFirst()
            }
        } else {
            if (retry == null) {
                retry = ChainSyncRetryModel(chain, sentBlock, error = records.error)
            } else {
                val shouldGiveUp = retry.retries >= maxRetry
                retry.apply {
                    retries += 1
                    error = records.error
                    giveUp = shouldGiveUp
                }
            }

            chainSyncRetryRepository.save(retry).awaitFirst()

            if (retry.giveUp) {
                val record = chainSyncRecordRepository.findByChain(chain).awaitFirstOrNull()
                if (record != null) {
                    val chainSyncRecordDao = ChainSyncRecordModel(
                        records.chainName,
                        records.time,
                        records.endpoint.url,
                        retry.block,
                        records.success,
                        records.error
                    )
                    chainSyncRecordRepository.save(chainSyncRecordDao).awaitFirst()
                }
            }
        }
    }
}
