package co.nilin.opex.chainscanner.preferences

data class Preferences(
    var chains: List<Chain> = emptyList(),
    var currencies: List<Currency> = emptyList()
)
