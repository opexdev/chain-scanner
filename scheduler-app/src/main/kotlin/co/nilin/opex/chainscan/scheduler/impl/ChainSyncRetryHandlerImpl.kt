package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.api.ChainSyncRetryHandler
import co.nilin.opex.chainscan.scheduler.model.ChainSyncRetryModel
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRetry
import co.nilin.opex.chainscan.scheduler.repository.ChainSyncRetryRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component

@Component
class ChainSyncRetryHandlerImpl(
    private val chainSyncRetryRepository: ChainSyncRetryRepository
) : ChainSyncRetryHandler {
    override suspend fun save(chainSyncRetry: ChainSyncRetry) {
        chainSyncRetryRepository.save(
            ChainSyncRetryModel(
                chainSyncRetry.chain,
                chainSyncRetry.startBlock,
                chainSyncRetry.endBlock,
                chainSyncRetry.retries,
                chainSyncRetry.synced,
                chainSyncRetry.giveUp,
                chainSyncRetry.error,
                chainSyncRetry.id
            )
        ).awaitSingle()
    }

    override suspend fun findAllActive(chainName: String): List<ChainSyncRetry> {
        return chainSyncRetryRepository.finByChainNameWhereNoGiveUp(chainName).map {
            ChainSyncRetry(
                it.chain,
                it.startBlock,
                it.endBlock,
                it.retries,
                it.synced,
                it.giveUp,
                it.error,
                it.id
            )
        }.collectList().awaitSingle()
    }
}
