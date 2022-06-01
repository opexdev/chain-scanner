package co.nilin.opex.chainscan.core.spi

interface AddressChecksumer {
    fun makeValid(address: String): String
}
