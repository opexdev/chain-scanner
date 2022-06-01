package co.nilin.opex.chainscan.scheduler.db.repository

import co.nilin.opex.chainscan.scheduler.db.dao.ChainSyncRecordModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ChainSyncRecordRepository : ReactiveCrudRepository<ChainSyncRecordModel, Long> {
    fun findByChain(chainName: String): Flux<ChainSyncRecordModel>
}
