package co.nilin.opex.chainscan.scheduler.model


import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("token_addresses")
data class TokenAddressModel(
    @Id var symbol: String,
    val chainName: String,
    val address: String,
    val memo: String?
)
