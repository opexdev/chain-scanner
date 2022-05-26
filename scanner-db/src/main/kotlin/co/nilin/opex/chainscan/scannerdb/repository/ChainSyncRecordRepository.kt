package co.nilin.opex.chainscan.scannerdb.repository

import co.nilin.opex.chainscan.scannerdb.model.ChainSyncRecordModel
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.math.BigInteger
import java.time.LocalDateTime

@Repository
interface ChainSyncRecordRepository : ReactiveCrudRepository<ChainSyncRecordModel, String> {
    @Query("insert into chain_sync_records values(:syncTime, :endpointUrl, :blockNumber)")
    fun insert(syncTime: LocalDateTime, endpointUrl: String, blockNumber: BigInteger): Mono<ChainSyncRecordModel>
}
