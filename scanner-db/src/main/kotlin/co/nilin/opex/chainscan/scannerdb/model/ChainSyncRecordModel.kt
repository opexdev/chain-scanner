package co.nilin.opex.chainscan.scannerdb.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigInteger
import java.time.LocalDateTime

@Table("chain_sync_records")
data class ChainSyncRecordModel(
    val consumerId: Long,
    val syncTime: LocalDateTime,
    val blockNumber: BigInteger,
    @Id var id: Long? = null
)
