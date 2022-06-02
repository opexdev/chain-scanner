package co.nilin.opex.chainscan.scheduler.db.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("chain_scanners")
data class ChainScannerModel(
    val chainName: String,
    val url: String,
    val maxBlockRange: Int = 10,
    val delayOnRateLimit: Int = 0,
    @Id val id: Long? = null
)
