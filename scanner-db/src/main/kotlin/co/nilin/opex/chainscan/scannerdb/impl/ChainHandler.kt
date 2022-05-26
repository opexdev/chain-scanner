package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.Chain
import co.nilin.opex.chainscan.core.model.Endpoint
import co.nilin.opex.chainscan.core.spi.ChainLoader
import co.nilin.opex.chainscan.scannerdb.repository.ChainRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrElse
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component

@Component
class ChainHandler(private val chainRepository: ChainRepository) : ChainLoader {
    override suspend fun addChain(name: String, addressType: String): Chain {
        val chain = chainRepository.findByName(name).awaitFirstOrNull()
        assert(chain == null)
        chainRepository.insert(name).awaitFirstOrNull()
        val model = chainRepository.findByName(name).awaitFirst()
        return Chain(model.name, emptyList())
    }

    override suspend fun fetchAllChains(): List<Chain> {
        return chainRepository.findAll()
            .collectList()
            .awaitFirstOrElse { emptyList() }
            .map { c ->
                val endpoints = chainRepository.findEndpointsByName(c.name).map { Endpoint(it.url) }.toList()
                Chain(c.name, endpoints)
            }
    }

    override suspend fun fetchChainInfo(chain: String): Chain {
        val chainDao = chainRepository.findByName(chain).awaitSingle()
        val endpoints = chainRepository.findEndpointsByName(chain).map { Endpoint(it.url) }.toList()
        return Chain(chainDao.name, endpoints)
    }
}
