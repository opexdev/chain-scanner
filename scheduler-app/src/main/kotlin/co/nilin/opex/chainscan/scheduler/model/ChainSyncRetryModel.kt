package co.nilin.opex.chainscan.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigInteger

@Table("chain_sync_retry")
data class ChainSyncRetryModel(
    val chain: String,
    val startBlock: BigInteger,
    val endBlock: BigInteger,
    val retries: Int = 0,
    val synced: Boolean = false,
    val giveUp: Boolean = false,
    val error: String? = null,
    @Id var id: Long? = null
)
