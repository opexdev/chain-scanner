package co.nilin.opex.chainscan.scheduler.db.dao

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("chain_sync_schedules")
data class ChainSyncScheduleModel(
    val chain: String,
    val retryTime: LocalDateTime,
    val delay: Long,
    val errorDelay: Long,
    val timeout: Long = 30000,
    val enabled: Boolean = true,
    @Id val id: Long? = null
)
