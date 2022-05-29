package co.nilin.opex.chainscan.core.spi

interface AddressAdapter {
    fun makeValid(address: String): String
}
