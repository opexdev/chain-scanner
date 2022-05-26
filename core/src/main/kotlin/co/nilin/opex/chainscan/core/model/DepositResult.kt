package co.nilin.opex.chainscan.core.model

data class DepositResult(
    val latestBlock: Long,
    val deposits: List<Deposit>
)
