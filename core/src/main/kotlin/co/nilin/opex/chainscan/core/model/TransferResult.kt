package co.nilin.opex.chainscan.core.model

data class TransferResult(
    val chainSyncRecord: ChainSyncRecord,
    val transfers: List<Transfer>
)
