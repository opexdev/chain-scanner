package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.ChainSyncRetry

interface ChainSyncRetryHandler {
    suspend fun save(chainSyncRetry: ChainSyncRetry)
    suspend fun findAllActive(chainName: String): List<ChainSyncRetry>
}
