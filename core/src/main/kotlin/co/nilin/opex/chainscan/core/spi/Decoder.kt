package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.Transfer

fun interface Decoder<T> {
    fun invoke(input: T): List<Transfer>
}
