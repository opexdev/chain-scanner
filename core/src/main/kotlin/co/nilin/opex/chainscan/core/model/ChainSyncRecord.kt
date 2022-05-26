package co.nilin.opex.chainscan.core.model

import java.math.BigInteger
import java.time.LocalDateTime

data class ChainSyncRecord(
    val id: Long?,
    val syncTime: LocalDateTime,
    val endpointUrl: String,
    val blockNumber: BigInteger
)
