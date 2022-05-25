package co.nilin.opex.chainscan.scheduler.po

data class DepositResult(
    val latestBlock: Long,
    val deposits: List<Deposit>
)