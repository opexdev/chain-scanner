package co.nilin.opex.chainscanner.preferences

data class Scanner(var url: String = "", var maxBlockRange: Int = 30, var delayOnRateLimit: Int = 300)
