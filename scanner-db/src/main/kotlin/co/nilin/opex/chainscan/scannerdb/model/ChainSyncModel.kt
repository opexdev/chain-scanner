package co.nilin.opex.chainscan.scannerdb.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("chain_sync_records")
data class ChainSyncRecordModel(
    @Id @Column("chain") val chain: String,
    val time: LocalDateTime,
    @Column("endpoint_url") val endpointUrl: String,
    @Column("latest_block") val latestBlock: Long?,
    val success: Boolean,
    val error: String?
)
