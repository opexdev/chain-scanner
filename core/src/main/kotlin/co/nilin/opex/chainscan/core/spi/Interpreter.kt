package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.Transfer

interface Interpreter<T> {
    fun interpret(tx: T): Transfer?
}
