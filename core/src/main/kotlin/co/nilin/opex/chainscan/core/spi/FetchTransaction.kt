package co.nilin.opex.chainscan.core.spi

import java.math.BigInteger

interface FetchTransaction<T> {
    suspend fun getTransactions(startBlock: BigInteger, endBlock: BigInteger): List<T>
}
