package co.nilin.opex.bcgateway.ports.postgres.impl

import co.nilin.opex.chainscan.scheduler.model.ChainSyncRecordModel
import co.nilin.opex.chainscan.scheduler.model.ChainSyncRetryModel
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule
import co.nilin.opex.chainscan.scheduler.repository.ChainSyncRecordRepository
import co.nilin.opex.chainscan.scheduler.repository.ChainSyncRetryRepository
import co.nilin.opex.chainscan.scheduler.spi.ChainSyncRetryHandler
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