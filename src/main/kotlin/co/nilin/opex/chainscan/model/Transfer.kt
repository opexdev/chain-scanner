package co.nilin.opex.chainscan.model

data class Transfer(
    val txHash: String,
    val from: String,
    val to: String,
    val isTokenTransfer: Boolean,
    val token: String? = null
)
