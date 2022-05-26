package co.nilin.opex.chainscan.scannerdb.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigInteger
import java.time.LocalDateTime

@Table("chain_sync_records")
data class ChainSyncRecordModel(
    @Id val id: Long,
    val syncTime: LocalDateTime,
    val endpointUrl: String,
    val blockNumber: BigInteger
)