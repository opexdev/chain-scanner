package co.nilin.opex.chainscanner.bitcoin.service

import co.nilin.opex.chainscanner.core.spi.AddressChecksumer
import org.springframework.stereotype.Component

@Component
class AddressChecksumerImpl : AddressChecksumer {
    override fun makeValid(address: String) = address
}
