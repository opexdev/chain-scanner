package co.nilin.opex.chainscan.core.model

data class TransfersRequest(
    val startBlock: Long,
    val endBlock: Long,
    val addresses: List<String>
)