package co.nilin.opex.chainscan.core.model

import java.math.BigInteger

data class TransfersRequest(
    val startBlock: BigInteger?,
    val endBlock: BigInteger?,
    val addresses: List<String>?
)
