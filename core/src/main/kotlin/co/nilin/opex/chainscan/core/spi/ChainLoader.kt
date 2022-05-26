package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.Chain

interface ChainLoader {
    suspend fun addChain(name: String, addressType: String): Chain
    suspend fun fetchAllChains(): List<Chain>
    suspend fun fetchChainInfo(chain: String): Chain
}
