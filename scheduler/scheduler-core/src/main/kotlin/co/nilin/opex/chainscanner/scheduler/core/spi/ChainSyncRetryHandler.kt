package co.nilin.opex.chainscanner.scheduler.core.spi

import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRetry
import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import java.math.BigInteger

interface ChainSyncRetryHandler {
    suspend fun save(chainSyncRetry: ChainSyncRetry)
    suspend fun findByChainAndBlockNumber(chainName: String, blockNumber: BigInteger): ChainSyncRetry?
    suspend fun findAllActive(chainName: String): List<ChainSyncRetry>
    suspend fun increaseRetryCounter(chainSyncRetry: ChainSyncRetry, sch: ChainSyncSchedule, error: String?) {
        val retries = chainSyncRetry.retries + 1
        save(chainSyncRetry.copy(retries = retries, giveUp = retries >= sch.maxRetries, error = error))
    }
}
