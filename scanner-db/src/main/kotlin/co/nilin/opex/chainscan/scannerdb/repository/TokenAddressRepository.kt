package co.nilin.opex.chainscan.scannerdb.repository

import co.nilin.opex.chainscan.scannerdb.model.TokenAddressModel
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface TokenAddressRepository : ReactiveCrudRepository<TokenAddressModel, String> {
    @Query("insert into token_addresses values (:symbol, :address, :memo) on conflict do nothing")
    fun insert(symbol: String, address: String, memo: String?): Mono<TokenAddressModel>

    fun deleteBySymbol(symbol: String): Mono<Int>
}
