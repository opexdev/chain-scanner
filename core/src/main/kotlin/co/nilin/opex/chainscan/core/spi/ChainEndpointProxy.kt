package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.ChainSyncRecord

interface ChainEndpointProxy {
    data class DepositFilter(val startBlock: Long?, val endBlock: Long?, val tokenAddresses: List<String>?)

    suspend fun syncTransfers(filter: DepositFilter): ChainSyncRecord
}
