package co.nilin.opex.chainscanner.core.model

data class WatchListItem(
    val symbol: String,
    val name: String,
    val address: String,
    val id: Long? = null
)
