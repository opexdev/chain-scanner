package co.nilin.opex.chainscanner.scheduler.db.repository

import co.nilin.opex.chainscanner.scheduler.db.model.ChainSyncRecordModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ChainSyncRecordRepository : ReactiveCrudRepository<ChainSyncRecordModel, Long> {
    fun findByChain(chainName: String): Flux<ChainSyncRecordModel>
}
