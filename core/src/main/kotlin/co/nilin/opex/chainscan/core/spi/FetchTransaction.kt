package co.nilin.opex.chainscan.core.spi

import java.math.BigInteger

interface FetchTransaction<T> {
    suspend fun getTransactions(blockNumber: BigInteger): T
}
