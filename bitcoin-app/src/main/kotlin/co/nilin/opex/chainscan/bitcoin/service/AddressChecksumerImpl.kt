package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.core.spi.AddressChecksumer
import org.springframework.stereotype.Component

@Component
class AddressChecksumerImpl : AddressChecksumer {
    override fun makeValid(address: String) = address
}
