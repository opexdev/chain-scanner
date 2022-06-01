package co.nilin.opex.chainscan.scheduler.po

import java.time.LocalDateTime

data class ChainSyncSchedule(
    val chainName: String,
    val retryTime: LocalDateTime,
    val delay: Long,
    val errorDelay: Long,
    val timeout: Long = 30000,
    val enabled: Boolean = true,
    val id: Long? = null
)
