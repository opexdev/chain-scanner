package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.model.ChainEndpointModel
import co.nilin.opex.chainscan.scheduler.po.Endpoint
import co.nilin.opex.chainscan.scheduler.repository.ChainEndpointRepository
import co.nilin.opex.chainscan.scheduler.spi.ChainEndpointHandler
import co.nilin.opex.chainscan.scheduler.spi.ChainEndpointProxy
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ChainEndpointHandlerImpl(
    private val webClient: WebClient,
    private val endpointRepository: ChainEndpointRepository
) : ChainEndpointHandler {
    override suspend fun addEndpoint(chainName: String, url: String, username: String?, password: String?) {
        endpointRepository.save(ChainEndpointModel(null, chainName, url, username, password)).awaitFirstOrNull()
    }

    override suspend fun deleteEndpoint(chainName: String, url: String) {
        endpointRepository.deleteByChainNameAndUrl(chainName, url).awaitFirstOrNull()
    }

    override suspend fun findChainEndpointProxy(chainName: String): ChainEndpointProxy {
        val endpoints =
            endpointRepository.findEndpointsByName(chainName).map { Endpoint(it.url) }.collectList().awaitFirst()
        return ChainEndpointProxyImpl(chainName, endpoints, webClient)
    }
}
