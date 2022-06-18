package co.nilin.opex.chainscanner.scheduler.core.po

import java.time.LocalDateTime

data class ChainSyncSchedule(
    val chainName: String,
    val executeTime: LocalDateTime,
    val delay: Long,
    val errorDelay: Long,
    val timeout: Long = 30,
    val enabled: Boolean = true,
    val confirmations: Int = 0,
    val maxRetries: Int = 5,
    val id: Long? = null
)
