package co.nilin.opex.chainscan.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("chain_sync_schedules")
data class ChainSyncScheduleModel(
    @Id @Column("chain") val chain: String,
    @Column("retry_time") val retryTime: LocalDateTime,
    var delay: Long,
    @Column("error_delay") var errorDelay: Long
)
