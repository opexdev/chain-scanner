package co.nilin.opex.chainscan.core.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.math.BigInteger

@JsonInclude(JsonInclude.Include.NON_NULL)
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
