package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.Wallet
import co.nilin.opex.chainscan.core.spi.Decoder
import co.nilin.opex.chainscan.tron.data.Contract
import co.nilin.opex.chainscan.tron.data.TransactionResponse
import co.nilin.opex.chainscan.tron.utils.asTronAddress
import co.nilin.opex.chainscan.tron.utils.tryOrElse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger

private const val TRC20_TRANSFER_METHOD_SIG = "a9059cbb000000000000000000000000"
private const val TRIGGER_SMART_CONTRACT = "TriggerSmartContract"
private const val TRANSFER_CONTRACT = "TransferContract"

@Component
class TronDecoder(@Value("\${app.chain-name}") private val chainName: String) : Decoder<TransactionResponse> {
    private fun handleTriggerContract(hash: String, contract: Contract): Transfer? {
        val params = contract.parameter.value
        val data = params.data ?: return null
        if (!data.startsWith(TRC20_TRANSFER_METHOD_SIG)) return null
        val toAddress = data.substring(30, 72).asTronAddress()
        val amount = tryOrElse(BigDecimal.ZERO) { BigInteger(data.substring(72), 16).toBigDecimal() }
        return Transfer(
            hash,
            BigInteger.ZERO,
            Wallet(params.from?.asTronAddress()!!),
            Wallet(toAddress),
            true,
            amount,
            params.contractAddress?.asTronAddress()!!,
            chainName
        )
    }

    private fun handleTransferContract(hash: String, contract: Contract): Transfer {
        return with(contract.parameter.value) {
            Transfer(
                hash,
                BigInteger.ZERO,
                Wallet(from?.asTronAddress()!!),
                Wallet(to?.asTronAddress()!!),
                false,
                amount?.toBigDecimal()!!,
                chainName,
            )
        }
    }

    override fun invoke(input: TransactionResponse): List<Transfer> {
        val transfers = arrayListOf<Transfer>()
        input.rawData.contract.forEach {
            when (it.type) {
                TRIGGER_SMART_CONTRACT -> handleTriggerContract(input.txID, it)?.let { t -> transfers.add(t) }
                TRANSFER_CONTRACT -> transfers.add(handleTransferContract(input.txID, it))
            }
        }
        return transfers
    }
}