package co.nilin.opex.chainscanner.scheduler.core.po

import java.math.BigDecimal
import java.math.BigInteger

data class Transfer(
    val txHash: String,
    val blockNumber: BigInteger,
    val receiver: Wallet,
    val isTokenTransfer: Boolean,
    val amount: BigDecimal,
    val chain: String,
    val tokenAddress: String? = null,
    val id: Long? = null
)
