package co.nilin.opex.chainscan.scannerdb.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.math.BigInteger

@Table("deposits")
data class DepositModel(
    @Id val id: Long?,
    val txHash: String,
    val blockNumber: BigInteger,
    val depositor: String,
    val depositorMemo: String?,
    val amount: BigDecimal,
    val token: Boolean,
    val tokenAddress: String?
)
