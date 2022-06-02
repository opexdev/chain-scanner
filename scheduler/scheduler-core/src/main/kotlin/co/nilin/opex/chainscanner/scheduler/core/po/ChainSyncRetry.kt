package co.nilin.opex.chainscanner.scheduler.core.po

import java.math.BigInteger

data class ChainSyncRetry(
    val chain: String,
    val blockNumber: BigInteger,
    val retries: Int = 0,
    val maxRetries: Int = 5,
    val synced: Boolean = false,
    val giveUp: Boolean = false,
    val error: String? = null,
    var id: Long? = null
)
