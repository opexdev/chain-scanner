package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.spi.AddressAdapter
import org.springframework.stereotype.Component

@Component
class AddressAdapterImpl : AddressAdapter {
    override fun makeValid(address: String) = address
}