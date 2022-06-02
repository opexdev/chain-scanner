package co.nilin.opex.chainscanner.scheduler.core.po

data class ChainScanner(
    val chainName: String,
    val url: String,
    val maxBlockRange: Int = 10,
    val delayOnRateLimit: Int = 0,
    val id: Long? = null
)
