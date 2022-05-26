package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule
import co.nilin.opex.chainscan.scheduler.repository.ChainSyncRetryRepository
import co.nilin.opex.chainscan.scheduler.spi.ChainSyncRetryHandler
import org.springframework.stereotype.Component

@Component
class ChainSyncRetryHandlerImpl(
    private val chainSyncRetryRepository: ChainSyncRetryRepository
) : ChainSyncRetryHandler {
    private val maxRetry = 5

    override suspend fun handleNextTry(syncSchedule: ChainSyncSchedule, sentBlock: Long) {
        val chain = syncSchedule.chainName
    }
}
