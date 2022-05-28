package co.nilin.opex.chainscan.scheduler.po

import java.math.BigInteger

data class TransferResult(
    val fromBlockNumber: BigInteger,
    val toBlockNumber: BigInteger,
    val transfers: List<Transfer>
)
