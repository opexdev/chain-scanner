package co.nilin.opex.chainscanner.preferences

data class Chain(
    var name: String = "",
    val scanners: List<Scanner> = emptyList(),
    var schedule: ChainSyncSchedule = ChainSyncSchedule()
)
