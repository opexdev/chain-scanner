package co.nilin.opex.chainscan.scannerdb.repository

import co.nilin.opex.chainscan.scannerdb.model.ChainEndpointModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ChainEndpointRepository : ReactiveCrudRepository<ChainEndpointModel, Long> {
    fun deleteByChainNameAndUrl(chainName: String, url: String): Mono<Int>
    fun findEndpointsByName(chainName: String): Flux<ChainEndpointModel>
}
