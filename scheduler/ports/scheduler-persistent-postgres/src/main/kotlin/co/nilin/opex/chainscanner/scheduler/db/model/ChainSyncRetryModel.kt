package co.nilin.opex.chainscanner.scheduler.db.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigInteger

@Table("chain_sync_retry")
data class ChainSyncRetryModel(
    val chain: String,
    val blockNumber: BigInteger,
    val retries: Int = 0,
    val maxRetries: Int = 0,
    val synced: Boolean = false,
    val giveUp: Boolean = false,
    val error: String? = null,
    @Id var id: Long? = null
)
