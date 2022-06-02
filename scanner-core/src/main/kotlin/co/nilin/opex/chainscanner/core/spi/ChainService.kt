package co.nilin.opex.chainscanner.core.spi

import java.math.BigInteger

interface ChainService<T> {
    suspend fun getTransactions(blockNumber: BigInteger): T
    suspend fun getLatestBlock(): BigInteger
}
