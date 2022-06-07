package co.nilin.opex.chainscanner.preferences

data class Preferences(
    var addressTypes: List<AddressType> = emptyList(),
    var chains: List<Chain> = emptyList(),
    var currencies: List<Currency> = emptyList(),
    var markets: List<Market> = emptyList(),
    var userLimits: List<UserLimit> = emptyList(),
    var system: System = System()
)
