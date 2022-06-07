package co.nilin.opex.chainscanner.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
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
    @JsonIgnore val id: Long? = null
)
