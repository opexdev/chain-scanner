package co.nilin.opex.chainscanner.scheduler.core.spi

import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRetry
import java.math.BigInteger

interface ChainSyncRetryHandler {
    suspend fun save(chainSyncRetry: ChainSyncRetry)
    suspend fun findByChainAndBlockNumber(chainName: String, blockNumber: BigInteger): ChainSyncRetry?
    suspend fun findAllActive(chainName: String): List<ChainSyncRetry>
    suspend fun markAsSynced(chainSyncRetry: ChainSyncRetry) {
        val retries = chainSyncRetry.retries + 1
        save(chainSyncRetry.copy(retries = retries, synced = true))
    }
}
