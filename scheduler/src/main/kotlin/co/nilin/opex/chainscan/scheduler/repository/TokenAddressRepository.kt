package co.nilin.opex.chainscan.scheduler.repository

import co.nilin.opex.chainscan.scheduler.model.TokenAddressModel
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface TokenAddressRepository : ReactiveCrudRepository<TokenAddressModel, String> {
    fun findBySymbol(symbol: String): Flux<TokenAddressModel>

    @Query("insert into token_addresses values (:symbol, :chain_name, :address, :memo) on conflict do nothing")
    fun insert(symbol: String, chainName: String, address: String, memo: String?): Mono<TokenAddressModel>

    @Query("delete from token_addresses where name = :name")
    fun deleteByName(symbol: String): Mono<Int>
}
