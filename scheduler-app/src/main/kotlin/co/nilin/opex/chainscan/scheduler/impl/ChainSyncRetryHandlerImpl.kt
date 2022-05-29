package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.api.ChainSyncRetryHandler
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRetry
import co.nilin.opex.chainscan.scheduler.repository.ChainSyncRetryRepository
import org.springframework.stereotype.Component

@Component
class ChainSyncRetryHandlerImpl(
    private val chainSyncRetryRepository: ChainSyncRetryRepository
) : ChainSyncRetryHandler {
    private val maxRetry = 5

    override suspend fun save(chainSyncRetry: ChainSyncRetry) {
        TODO("Not yet implemented")
    }

    override suspend fun findAllActive(chainName: String): List<ChainSyncRetry> {
        TODO("Not yet implemented")
    }
}
