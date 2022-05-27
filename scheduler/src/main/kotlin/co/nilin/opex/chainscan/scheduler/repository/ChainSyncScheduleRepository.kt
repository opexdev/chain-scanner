package co.nilin.opex.chainscan.scheduler.repository

import co.nilin.opex.chainscan.scheduler.model.ChainSyncScheduleModel
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
interface ChainSyncScheduleRepository : ReactiveCrudRepository<ChainSyncScheduleModel, String> {
    @Query("select * from chain_sync_schedules where retry_time <= :time and enabled")
    fun findActiveSchedule(time: LocalDateTime): Flow<ChainSyncScheduleModel>
}
