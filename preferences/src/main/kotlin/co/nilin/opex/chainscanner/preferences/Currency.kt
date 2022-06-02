package co.nilin.opex.chainscanner.preferences

data class Currency(
    var symbol: String = "",
    var name: String = "",
    var implementations: List<CurrencyImplementation> = emptyList(),
)
