package co.nilin.opex.chainscanner.scheduler.core.po

import java.math.BigInteger
import java.time.LocalDateTime

data class ChainSyncRecord(
    val chain: String,
    val syncTime: LocalDateTime,
    val blockNumber: BigInteger,
    val id: Long? = null
)
