package co.nilin.opex.chainscan.scheduler.spi

import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord

interface ChainEndpointProxy {
    data class DepositFilter(val startBlock: Long?, val endBlock: Long?, val tokenAddresses: List<String>?)
    suspend fun syncTransfers(filter: DepositFilter): ChainSyncRecord
}