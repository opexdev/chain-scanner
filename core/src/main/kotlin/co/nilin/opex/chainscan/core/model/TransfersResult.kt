package co.nilin.opex.chainscan.core.model

data class TransfersResult(
    val latestBlock: Long,
    val transfers: List<Transfer>
)