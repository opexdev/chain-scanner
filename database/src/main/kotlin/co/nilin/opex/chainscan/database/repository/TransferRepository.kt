package co.nilin.opex.chainscan.database.repository

import co.nilin.opex.chainscan.database.model.TransferModel
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.math.BigInteger

@Repository
interface TransferRepository : ReactiveCrudRepository<TransferModel, Long> {
    @Query("select * from transfers where token_address in (:tokenAddresses)")
    fun findByTokenAddress(tokenAddresses: List<String>): Flux<TransferModel>

    @Query("delete from transfers where block_number <= :blockNumber")
    fun clearCache(blockNumber: BigInteger): Flux<TransferModel>
}
