package co.nilin.opex.chainscan.scheduler.po

import java.math.BigInteger

data class TransferResult(
    val blockNumber: BigInteger,
    val transfers: List<Transfer>
)
