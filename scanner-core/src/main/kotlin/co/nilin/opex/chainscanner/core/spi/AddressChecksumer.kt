package co.nilin.opex.chainscanner.core.spi

interface AddressChecksumer {
    fun makeValid(address: String): String
}
