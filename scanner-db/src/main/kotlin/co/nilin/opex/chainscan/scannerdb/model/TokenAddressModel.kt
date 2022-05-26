package co.nilin.opex.chainscan.scannerdb.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("token_addresses")
data class TokenAddressModel(
    @Id val id: Long?,
    val symbol: String,
    val chainName: String,
    val address: String,
    val memo: String?
)
