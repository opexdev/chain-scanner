package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.tron.data.Contract
import co.nilin.opex.chainscan.tron.data.TransactionResponse
import co.nilin.opex.chainscan.tron.data.TransactionType
import co.nilin.opex.chainscan.tron.utils.tryOrElse
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger

private const val TRC20_TRANSFER_METHOD_SIG = "0xa9059cbb000000000000000000000000"
private const val TRX_TRANSFER_METHOD_SIG = "0x"

@Component
class TronInterpreter {

    private fun handleTriggerContract(hash: String, contract: Contract): Transfer? {
        val data = contract.parameter.value.data
        if (data == null || !data.startsWith(TRC20_TRANSFER_METHOD_SIG))
            return null

        val toAddress = tryOrElse(null) { data.substring(30, 72) }
        val amount = tryOrElse(BigDecimal.ZERO) { BigDecimal(BigInteger(data.substring(72), 16)) }
    }

    private fun handleTransferContract(hash: String, contract: Contract): Transfer {
        return with(contract.parameter.value) {
            Transfer(hash, from, to, false, null, amount?.toBigDecimal() ?: BigDecimal.ZERO)
        }
    }

    fun interpret(tx: TransactionResponse): List<Transfer> {
        val transfers = arrayListOf<Transfer>()
        tx.rawData.contract.forEach {
            when (it.type) {
                TransactionType.TriggerSmartContract -> {

                }
                TransactionType.TransferContract -> TODO()
            }
        }
    }

}