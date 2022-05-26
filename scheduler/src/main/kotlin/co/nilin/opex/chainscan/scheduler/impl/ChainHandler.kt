package co.nilin.opex.bcgateway.ports.postgres.impl

import co.nilin.opex.chainscan.scheduler.po.Chain
import co.nilin.opex.chainscan.scheduler.repository.ChainRepository
import co.nilin.opex.chainscan.scheduler.spi.ChainLoader
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
        return Chain(model.name)
    }

    override suspend fun fetchAllChains(): List<Chain> {
        return chainRepository.findAll()
            .collectList()
            .awaitFirstOrElse { emptyList() }
            .map { c -> Chain(c.name) }
    }

    override suspend fun fetchChainInfo(chain: String): Chain {
        val chainDao = chainRepository.findByName(chain).awaitSingle()
        return Chain(chainDao.name)
    }
}
