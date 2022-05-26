package co.nilin.opex.chainscan.core.model

data class TokenAddress(
    val symbol: String,
    val chainName: String,
    val address: String,
    val memo: String?
)
