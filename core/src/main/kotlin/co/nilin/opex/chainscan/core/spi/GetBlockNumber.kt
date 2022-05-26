package co.nilin.opex.chainscan.core.spi

import java.math.BigInteger

fun interface GetBlockNumber {
    suspend fun invoke(): BigInteger
}
