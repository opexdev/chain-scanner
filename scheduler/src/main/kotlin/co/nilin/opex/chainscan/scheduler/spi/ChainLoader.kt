package co.nilin.opex.chainscan.scheduler.spi

import co.nilin.opex.chainscan.scheduler.po.Chain

interface ChainLoader {
    suspend fun addChain(name: String, addressType: String): Chain
    suspend fun fetchAllChains(): List<Chain>
    suspend fun fetchChainInfo(chain: String): Chain
}
