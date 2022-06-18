package co.nilin.opex.chainscanner.scheduler.db.repository

import co.nilin.opex.chainscanner.scheduler.db.model.ChainModel
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ChainRepository : ReactiveCrudRepository<ChainModel, String> {
    @Query("insert into chains values (:name)")
    fun insert(name: String): Mono<Int>
}
