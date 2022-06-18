package co.nilin.opex.chainscanner.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.math.BigInteger

@Table("transfers")
data class TransferModel(
    val txHash: String,
    val blockNumber: BigInteger,
    val receiverAddress: String,
    val receiverMemo: String?,
    val isTokenTransfer: Boolean,
    val amount: BigDecimal,
    val chain: String,
    val tokenAddress: String? = null,
    @Id val id: Long? = null
)
