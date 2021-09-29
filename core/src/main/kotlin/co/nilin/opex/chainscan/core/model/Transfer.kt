package co.nilin.opex.chainscan.core.model

import java.math.BigDecimal

data class Transfer(
    var txHash: String,
    var from: String,
    var to: String,
    var isTokenTransfer: Boolean,
    var token: String? = null,
    var amount: BigDecimal
)
