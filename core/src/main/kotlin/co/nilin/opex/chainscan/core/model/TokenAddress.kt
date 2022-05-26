package co.nilin.opex.chainscan.core.model

data class TokenAddress(
    val symbol: String,
    val address: String,
    val id: Long? = null
)
