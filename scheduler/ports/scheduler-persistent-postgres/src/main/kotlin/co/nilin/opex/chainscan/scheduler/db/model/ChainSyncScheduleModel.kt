package co.nilin.opex.chainscan.scheduler.db.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("chain_sync_schedules")
data class ChainSyncScheduleModel(
    val chain: String,
    val executeTime: LocalDateTime,
    val delay: Long,
    val errorDelay: Long,
    val timeout: Long = 30,
    val enabled: Boolean = true,
    val confirmations: Int = 0,
    val maxRetries: Int = 5,
    @Id val id: Long? = null
)
