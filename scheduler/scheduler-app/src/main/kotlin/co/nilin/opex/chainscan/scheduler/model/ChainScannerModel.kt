package co.nilin.opex.chainscan.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("chain_scanners")
data class ChainScannerModel(
    val chainName: String,
    val url: String,
    val maxBlockRange: Int = 10,
    val confirmations: Int = 0,
    val rateLimitDelay: Int = 0,
    @Id val id: Long? = null
)
