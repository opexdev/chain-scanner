package co.nilin.opex.chainscan.scannerdb.repository

import co.nilin.opex.chainscan.scannerdb.model.TokenAddressModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface TokenAddressRepository : ReactiveCrudRepository<TokenAddressModel, Long> {
    fun deleteBySymbol(symbol: String): Mono<Int>
}
