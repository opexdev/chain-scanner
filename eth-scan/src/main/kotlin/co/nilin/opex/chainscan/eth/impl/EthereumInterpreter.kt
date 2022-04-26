package co.nilin.opex.chainscan.eth.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.Interpreter
import co.nilin.opex.chainscan.eth.utils.checksumAddress
import co.nilin.opex.chainscan.eth.utils.tryOrElse
import org.springframework.stereotype.Component
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigDecimal
import java.math.BigInteger

const val ERC20_TRANSFER_METHOD_SIG = "0xa9059cbb000000000000000000000000"
const val ETH_TRANSFER_METHOD_SIG = "0x"

@Component
class EthereumInterpreter : Interpreter<EthBlock.TransactionObject> {
    private fun isAssetTransfer(input: String) = input == ETH_TRANSFER_METHOD_SIG
    private fun isTokenTransfer(input: String) = input.length == 138 && input.startsWith(ERC20_TRANSFER_METHOD_SIG)

    private fun parseAssetTransfer(tx: EthBlock.TransactionObject): Transfer {
        if (!isAssetTransfer(tx.input)) throw IllegalArgumentException()
        val amount = tryOrElse(BigDecimal.ZERO) { BigDecimal(tx.value) }
        return Transfer(tx.hash, tx.from.checksumAddress(), tx.to.checksumAddress(), false, null, amount)
    }

    private fun parseTokenTransfer(tx: EthBlock.TransactionObject): Transfer {
        if (!isTokenTransfer(tx.input)) throw IllegalArgumentException()
        val receiver = tryOrElse("0x") { "0x${tx.input.substring(34, 74)}" }.checksumAddress()
        val amount = tryOrElse(BigDecimal.ZERO) { BigDecimal(BigInteger(tx.input.substring(74), 16)) }
        return Transfer(tx.hash, tx.from.checksumAddress(), receiver, true, tx.to.checksumAddress(), amount)
    }

    override fun interpret(tx: EthBlock.TransactionObject): Transfer? {
        return when {
            isAssetTransfer(tx.input) -> parseAssetTransfer(tx)
            isTokenTransfer(tx.input) -> parseTokenTransfer(tx)
            else -> null
        }
    }
}
