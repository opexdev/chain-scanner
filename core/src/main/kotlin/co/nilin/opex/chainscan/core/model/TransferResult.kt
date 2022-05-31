package co.nilin.opex.chainscan.core.model

import java.math.BigInteger

data class TransferResult(
    val blockNumber: BigInteger,
    val transfers: List<Transfer>
)
