package co.nilin.opex.chainscan.core.model

import java.math.BigInteger
import java.time.LocalDateTime

data class ChainSyncRecord(
    val syncTime: LocalDateTime,
    val consumerId: Long,
    val blockNumber: BigInteger,
    val id: Long? = null
)
