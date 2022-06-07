package co.nilin.opex.chainscanner.scheduler.db.repository

import co.nilin.opex.chainscanner.scheduler.db.model.ChainSyncRetryModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigInteger

@Repository
interface ChainSyncRetryRepository : ReactiveCrudRepository<ChainSyncRetryModel, Long> {
    fun findByGiveUpIsFalseAndSyncedIsFalseAndChain(chain: String): Flux<ChainSyncRetryModel>
    fun findByChainAndBlockNumber(chain: String, blockNumber: BigInteger): Mono<ChainSyncRetryModel>
}
