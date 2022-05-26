package co.nilin.opex.chainscan.core.model

import java.math.BigInteger
import java.time.LocalDateTime

data class ChainSyncRecord(
    val consumerId: Long,
    val syncTime: LocalDateTime,
    val blockNumber: BigInteger,
    val id: Long? = null
)
