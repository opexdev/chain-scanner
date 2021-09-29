package co.nilin.opex.chainscan.core.model

import java.math.BigDecimal

data class Transfer(
    val txHash: String,
    val from: String,
    val to: String,
    val value:BigDecimal,
    val isTokenTransfer: Boolean,
    val token: String? = null
)
