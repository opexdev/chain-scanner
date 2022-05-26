package co.nilin.opex.chainscan.scannerdb.repository

import co.nilin.opex.chainscan.scannerdb.model.ChainEndpointModel
import co.nilin.opex.chainscan.scannerdb.model.ChainModel
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ChainRepository : ReactiveCrudRepository<ChainModel, String> {
    @Query("insert into chains values (:name) on conflict do nothing")
    fun insert(name: String): Mono<Int>

    fun findByName(name: String): Mono<ChainModel>

    @Query("select * from chain_endpoints where chain_name = :name")
    fun findEndpointsByName(name: String): Flow<ChainEndpointModel>
}
