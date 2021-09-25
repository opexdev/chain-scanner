package co.nilin.opex.chainscan.service

import co.nilin.opex.chainscan.model.Transfer

interface Interpreter<T> {
    fun interpret(tx: T): Transfer?
}
