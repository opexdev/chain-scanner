package co.nilin.opex.chainscanner.database.repository

import co.nilin.opex.chainscanner.database.model.TransferModel
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigInteger

@Repository
interface TransferRepository : ReactiveCrudRepository<TransferModel, Long> {
    @Query("select * from transfers where is_token_transfer and token_address in (:tokenAddresses)")
    fun findByTokenAddress(tokenAddresses: List<String>): Flux<TransferModel>

    @Query("select * from transfers where not is_token_transfer")
    fun findAllNotTokenTransfers(): Flux<TransferModel>

    @Query("delete from transfers where block_number = :blockNumber")
    fun clearCache(blockNumber: BigInteger): Mono<Int>
}
