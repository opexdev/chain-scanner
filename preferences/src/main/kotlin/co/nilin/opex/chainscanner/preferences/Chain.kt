package co.nilin.opex.chainscanner.preferences

data class Chain(
    var name: String = "",
    var addressType: String = "",
    val scanners: List<Scanner> = emptyList(),
    var schedule: ChainSyncSchedule = ChainSyncSchedule()
)
