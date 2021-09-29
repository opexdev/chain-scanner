package co.nilin.opex.chainscan.eth.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.Interpreter
import org.springframework.stereotype.Component
import org.web3j.protocol.core.methods.response.EthBlock
import kotlin.math.pow

const val ERC20_TRANSFER_METHOD_ID = "0xa9059cbb000000000000000000000000"
const val ETH_TRANSFER_METHOD_ID = "0x"

@Component
class EthereumInterpreter : Interpreter<EthBlock.TransactionObject> {
    override fun interpret(tx: EthBlock.TransactionObject): Transfer? {
        return when {
            tx.input == ETH_TRANSFER_METHOD_ID -> {
                val amount = tx.value.toBigDecimal().divide((10.0).pow(18).toBigDecimal())
                Transfer(tx.hash, tx.from, tx.to, false, null, amount)
            }
            tx.input.startsWith(ERC20_TRANSFER_METHOD_ID) -> {
                val receiver = "0x${tx.input.substring(34, 74)}"
                val amount = tx.input.substring(74).toLong(16).toBigDecimal().divide((10.0).pow(18).toBigDecimal())
                Transfer(tx.hash, tx.from, receiver, true, tx.to, amount)
            }
            else -> null
        }
    }
}
