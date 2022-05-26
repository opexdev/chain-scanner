package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.Endpoint
import co.nilin.opex.chainscan.core.spi.ChainEndpointHandler
import co.nilin.opex.chainscan.scannerdb.model.ChainEndpointModel
import co.nilin.opex.chainscan.scannerdb.repository.ChainEndpointRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component

@Component
class ChainEndpointHandlerImpl(private val endpointRepository: ChainEndpointRepository) : ChainEndpointHandler {
    override suspend fun addEndpoint(url: String, apiKey: String?) {
        endpointRepository.save(ChainEndpointModel(url, apiKey)).awaitFirstOrNull()
    }

    override suspend fun deleteEndpoint(id: Long) {
        endpointRepository.deleteById(id).awaitFirstOrNull()
    }

    override suspend fun findAll(): List<Endpoint> {
        return endpointRepository.findAll().map { Endpoint(it.endpoint_url) }.collectList().awaitSingle()
    }
}
