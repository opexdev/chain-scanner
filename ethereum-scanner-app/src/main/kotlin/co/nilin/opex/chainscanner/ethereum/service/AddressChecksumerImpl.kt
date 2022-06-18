package co.nilin.opex.chainscanner.ethereum.service

import co.nilin.opex.chainscanner.core.spi.AddressChecksumer
import co.nilin.opex.chainscanner.ethereum.utils.checksumAddress
import org.springframework.stereotype.Component

@Component
class AddressChecksumerImpl : AddressChecksumer {
    override fun makeValid(address: String) = address.checksumAddress()
}
