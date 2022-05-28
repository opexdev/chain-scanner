package co.nilin.opex.chainscan.database.repository

import co.nilin.opex.chainscan.database.model.ChainEndpointModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ChainEndpointRepository : ReactiveCrudRepository<ChainEndpointModel, Long> {
    fun deleteByChainNameAndUrl(url: String): Mono<Int>
    fun findEndpointsByName(): Flux<ChainEndpointModel>
}
