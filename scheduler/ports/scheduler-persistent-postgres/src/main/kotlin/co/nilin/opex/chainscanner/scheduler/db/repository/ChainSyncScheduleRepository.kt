package co.nilin.opex.chainscanner.scheduler.db.repository

import co.nilin.opex.chainscanner.scheduler.db.model.ChainSyncScheduleModel
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
interface ChainSyncScheduleRepository : ReactiveCrudRepository<ChainSyncScheduleModel, Long> {
    @Query("select * from chain_sync_schedules where execute_time <= :dateTime and enabled")
    fun findActiveSchedule(dateTime: LocalDateTime): Flow<ChainSyncScheduleModel>

    fun findByChain(chain: String): Mono<ChainSyncScheduleModel>
}
