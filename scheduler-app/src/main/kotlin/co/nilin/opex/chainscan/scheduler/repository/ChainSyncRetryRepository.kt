package co.nilin.opex.chainscan.scheduler.repository

import co.nilin.opex.chainscan.scheduler.model.ChainSyncRetryModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ChainSyncRetryRepository : ReactiveCrudRepository<ChainSyncRetryModel, Long> {
    fun finByChainNameWhereNoGiveUp(chainName: String): Flux<ChainSyncRetryModel>
}
