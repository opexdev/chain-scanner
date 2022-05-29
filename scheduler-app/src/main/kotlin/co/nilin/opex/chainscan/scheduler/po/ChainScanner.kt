package co.nilin.opex.chainscan.scheduler.po

data class ChainScanner(
    val chainName: String,
    val url: String,
    val maxBlockRange: Int,
    val id: Long? = null
)
