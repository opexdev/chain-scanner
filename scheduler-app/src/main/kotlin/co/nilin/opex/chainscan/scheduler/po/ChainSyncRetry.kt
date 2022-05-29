package co.nilin.opex.chainscan.scheduler.po

import java.math.BigInteger

data class ChainSyncRetry(
    val chain: String,
    val startBlock: BigInteger,
    val endBlock: BigInteger,
    val retries: Int = 0,
    val synced: Boolean = false,
    val giveUp: Boolean = false,
    val error: String? = null,
    var id: Long? = null
)
