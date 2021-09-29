package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.Interpreter
import org.springframework.stereotype.Component

@Component
class BitcoinInterpreter : Interpreter<Any> {

    override fun interpret(tx: Any): Transfer? {
        TODO("Not yet implemented")
    }

}