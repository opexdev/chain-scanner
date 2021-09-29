package co.nilin.opex.chainscan.eth.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.Interpreter
import org.springframework.stereotype.Component
import org.web3j.protocol.core.methods.response.EthBlock

const val ERC20_TRANSFER_METHOD_ID = "0xa9059cbb000000000000000000000000"
const val ETH_TRANSFER_METHOD_ID = "0x"

@Component
class EthereumInterpreter : Interpreter<EthBlock.TransactionObject> {
    override fun interpret(tx: EthBlock.TransactionObject): Transfer? {
        val data = tx.input.trim()
        return when {
            data == ETH_TRANSFER_METHOD_ID -> Transfer(tx.hash, tx.from, tx.to, tx.value.toBigDecimal(), false)
            data.startsWith(ERC20_TRANSFER_METHOD_ID) -> Transfer(
                tx.hash,
                tx.from,
                "0x${data.substring(34, 74)}",
                tx.value.toBigDecimal(),
                true,
                tx.to
            )
            else -> null
        }
    }
}
