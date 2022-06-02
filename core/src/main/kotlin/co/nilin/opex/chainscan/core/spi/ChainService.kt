package co.nilin.opex.chainscan.core.spi

import java.math.BigInteger

interface ChainService<out T> {
    suspend fun getTransactions(blockNumber: BigInteger): T
    suspend fun getLatestBlock(): BigInteger
}
