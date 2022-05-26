package co.nilin.opex.chainscan.core.model

data class DepositResult(
    val chainSyncRecord: ChainSyncRecord,
    val deposits: List<Deposit>
)
