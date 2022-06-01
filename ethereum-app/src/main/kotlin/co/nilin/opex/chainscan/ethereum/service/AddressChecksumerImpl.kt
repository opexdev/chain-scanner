package co.nilin.opex.chainscan.ethereum.service

import co.nilin.opex.chainscan.core.spi.AddressChecksumer
import co.nilin.opex.chainscan.ethereum.utils.checksumAddress
import org.springframework.stereotype.Component

@Component
class AddressChecksumerImpl : AddressChecksumer {
    override fun makeValid(address: String) = address.checksumAddress()
}
