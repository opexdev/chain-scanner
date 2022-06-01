package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.Transfer

fun interface DataDecoder<in T> {
    suspend fun decode(input: T): List<Transfer>
}
