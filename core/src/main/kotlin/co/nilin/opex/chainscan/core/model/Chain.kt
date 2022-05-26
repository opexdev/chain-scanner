package co.nilin.opex.chainscan.core.model

import java.time.LocalDateTime

data class Endpoint(val url: String)
data class Chain(val name: String, val endpoints: List<Endpoint>)
data class ChainSyncSchedule(val chainName: String, val retryTime: LocalDateTime, val delay: Long, val errorDelay: Long)
data class ChainSyncRecord(
    val chainName: String,
    val time: LocalDateTime,
    val endpoint: Endpoint,
    val latestBlock: Long?,
    val success: Boolean,
    val error: String?,
    val records: List<Deposit>
)
