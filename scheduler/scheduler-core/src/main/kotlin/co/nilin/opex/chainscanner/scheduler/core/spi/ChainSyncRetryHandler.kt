package co.nilin.opex.chainscanner.scheduler.core.spi

import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncRetry
import java.math.BigInteger

interface ChainSyncRetryHandler {
    suspend fun save(chainSyncRetry: ChainSyncRetry)
    suspend fun findByChainAndBlockNumber(chainName: String, blockNumber: BigInteger): ChainSyncRetry?
    suspend fun findAllActive(chainName: String): List<ChainSyncRetry>
}
