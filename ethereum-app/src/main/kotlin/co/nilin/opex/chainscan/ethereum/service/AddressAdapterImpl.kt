package co.nilin.opex.chainscan.ethereum.service

import co.nilin.opex.chainscan.core.spi.AddressAdapter
import co.nilin.opex.chainscan.ethereum.utils.checksumAddress
import org.springframework.stereotype.Component

@Component
class AddressAdapterImpl : AddressAdapter {
    override fun makeValid(address: String) = address.checksumAddress()
}