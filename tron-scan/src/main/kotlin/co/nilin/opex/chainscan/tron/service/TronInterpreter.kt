package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.tron.data.Contract
import co.nilin.opex.chainscan.tron.data.TransactionResponse
import co.nilin.opex.chainscan.tron.utils.asTronAddress
import co.nilin.opex.chainscan.tron.utils.tryOrElse
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger

private const val TRC20_TRANSFER_METHOD_SIG = "a9059cbb000000000000000000000000"

@Component
class TronInterpreter {

    private fun handleTriggerContract(hash: String, contract: Contract): Transfer? {
        val params = contract.parameter.value
        val data = params.data
        if (data == null || !data.startsWith(TRC20_TRANSFER_METHOD_SIG))
            return null

        val toAddress = data.substring(30, 72).asTronAddress()
        val amount = tryOrElse(BigDecimal.ZERO) { BigDecimal(BigInteger(data.substring(72), 16)) }
        return Transfer(
            hash,
            params.from.asTronAddress(),
            toAddress,
            true,
            params.contractAddress.asTronAddress(),
            amount
        )
    }

    private fun handleTransferContract(hash: String, contract: Contract): Transfer {
        return with(contract.parameter.value) {
            Transfer(
                hash,
                from.asTronAddress(),
                to.asTronAddress(),
                false,
                null,
                amount?.toBigDecimal() ?: BigDecimal.ZERO
            )
        }
    }

    fun interpret(tx: TransactionResponse): List<Transfer> {
        val transfers = arrayListOf<Transfer>()
        tx.rawData.contract.forEach {
            when (it.type) {
                "TriggerSmartContract" -> handleTriggerContract(
                    tx.txID,
                    it
                )?.let { t -> transfers.add(t) }
                "TransferContract" -> transfers.add(handleTransferContract(tx.txID, it))
            }
        }
        return transfers
    }

}