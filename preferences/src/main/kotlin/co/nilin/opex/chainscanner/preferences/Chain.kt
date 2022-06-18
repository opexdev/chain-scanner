package co.nilin.opex.chainscanner.preferences

data class Chain(
    var name: String = "",
    var addressType: String = "",
    var scanners: List<Scanner> = emptyList(),
    var schedule: ChainSyncSchedule? = null
)
