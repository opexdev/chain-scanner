package co.nilin.opex.chainscan.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("chain_sync_schedules")
data class ChainSyncScheduleModel(
    val chain: String,
    val retryTime: LocalDateTime,
    val delay: Long,
    val errorDelay: Long,
    val enabled: Boolean,
    @Id val id: Long? = null
)
