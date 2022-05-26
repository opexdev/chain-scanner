package co.nilin.opex.chainscan.core.model

import java.math.BigDecimal
import java.math.BigInteger

data class Transfer(
    val txHash: String,
    val blockNumber: BigInteger,
    val from: Wallet,
    val to: Wallet,
    val isTokenTransfer: Boolean,
    val amount: BigDecimal,
    val chain: String,
    val tokenAddress: String? = null,
    val id: Long? = null
)
