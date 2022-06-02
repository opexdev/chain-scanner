package co.nilin.opex.chainscanner.core.spi

import co.nilin.opex.chainscanner.core.model.Transfer

fun interface DataDecoder<T> {
    suspend fun decode(input: T): List<Transfer>
}
