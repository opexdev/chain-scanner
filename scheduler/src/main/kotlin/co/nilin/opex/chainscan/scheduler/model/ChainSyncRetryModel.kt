package co.nilin.opex.chainscan.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigInteger

@Table("chain_sync_retry")
class ChainSyncRetryModel(
    val chain: String,
    val block: BigInteger,
    val retries: Int = 1,
    val synced: Boolean = false,
    val giveUp: Boolean = false,
    val error: String? = null,
    @Id var id: Long? = null
)
