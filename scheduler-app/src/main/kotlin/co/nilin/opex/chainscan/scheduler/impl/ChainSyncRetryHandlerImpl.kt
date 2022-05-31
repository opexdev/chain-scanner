package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.api.ChainSyncRetryHandler
import co.nilin.opex.chainscan.scheduler.dto.toModel
import co.nilin.opex.chainscan.scheduler.dto.toPlainObject
import co.nilin.opex.chainscan.scheduler.po.ChainSyncRetry
import co.nilin.opex.chainscan.scheduler.repository.ChainSyncRetryRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component

@Component
class ChainSyncRetryHandlerImpl(
    private val chainSyncRetryRepository: ChainSyncRetryRepository
) : ChainSyncRetryHandler {
    override suspend fun save(chainSyncRetry: ChainSyncRetry) {
        chainSyncRetryRepository.save(chainSyncRetry.toModel()).awaitSingle()
    }

    override suspend fun findAllActive(chainName: String): List<ChainSyncRetry> {
        return chainSyncRetryRepository.findByGiveUpIsFalseAndSyncedIsFalseAndChain(chainName)
            .map { it.toPlainObject() }.collectList().awaitSingle()
    }
}
