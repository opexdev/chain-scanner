package co.nilin.opex.chainscan.core.model

import java.math.BigDecimal
import java.math.BigInteger

data class Deposit(
    val id: Long?,
    val txHash: String,
    val blockNumber: BigInteger,
    val depositor: String,
    val depositorMemo: String?,
    val amount: BigDecimal,
    val chain: String,
    val token: Boolean,
    val tokenAddress: String?
)
