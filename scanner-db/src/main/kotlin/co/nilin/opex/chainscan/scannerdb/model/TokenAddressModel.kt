package co.nilin.opex.chainscan.scannerdb.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("token_addresses")
data class TokenAddressModel(
    val symbol: String,
    val address: String,
    @Id val id: Long? = null
)
