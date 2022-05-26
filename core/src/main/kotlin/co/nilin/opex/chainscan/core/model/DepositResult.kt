package co.nilin.opex.chainscan.core.model

import java.math.BigInteger

data class DepositResult(
    val latestBlock: BigInteger,
    val deposits: List<Deposit>
)
