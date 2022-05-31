package co.nilin.opex.chainscan.scheduler.po

data class ChainScanner(
    val chainName: String,
    val url: String,
    val maxBlockRange: Int = 10,
    val confirmations: Int = 0,
    val id: Long? = null
)
