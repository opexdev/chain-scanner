package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.spi.ChainEndpointHandler
import co.nilin.opex.chainscan.scannerdb.model.ChainEndpointModel
import co.nilin.opex.chainscan.scannerdb.repository.ChainEndpointRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component

@Component
class ChainEndpointHandlerImpl(private val endpointRepository: ChainEndpointRepository) : ChainEndpointHandler {
    override suspend fun addEndpoint(url: String, username: String?, password: String?) {
        endpointRepository.save(ChainEndpointModel(null, url, username, password)).awaitFirstOrNull()
    }

    override suspend fun deleteEndpoint(id: Long) {
        endpointRepository.deleteById(id).awaitFirstOrNull()
    }
}
