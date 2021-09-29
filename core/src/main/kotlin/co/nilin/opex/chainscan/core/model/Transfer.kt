package co.nilin.opex.chainscan.core.model

data class Transfer(
    val txHash: String,
    val from: String,
    val to: String,
    val isTokenTransfer: Boolean,
    val token: String? = null
)
