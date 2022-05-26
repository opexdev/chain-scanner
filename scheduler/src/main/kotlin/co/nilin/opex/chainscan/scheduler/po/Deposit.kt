package co.nilin.opex.chainscan.scheduler.po

import java.math.BigDecimal

data class Deposit(
    val id: Long?,
    val hash: String,
    val depositor: String,
    val depositorMemo: String?,
    val amount: BigDecimal,
    val chain: String,
    val token: Boolean,
    val tokenAddress: String?
)