package co.nilin.opex.chainscanner.tron.service

import co.nilin.opex.chainscanner.core.model.Transfer
import co.nilin.opex.chainscanner.core.model.Wallet
import co.nilin.opex.chainscanner.core.spi.DataDecoder
import co.nilin.opex.chainscanner.tron.data.BlockResponse
import co.nilin.opex.chainscanner.tron.data.Contract
import co.nilin.opex.chainscanner.tron.utils.asTronAddress
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger

private const val TRC20_TRANSFER_METHOD_SIG = "a9059cbb000000000000000000000000"
private const val TRIGGER_SMART_CONTRACT = "TriggerSmartContract"
private const val TRANSFER_CONTRACT = "TransferContract"

@Component
class TronDataDecoder(@Value("\${app.chain.name}") private val chainName: String) : DataDecoder<BlockResponse> {
    private fun handleTriggerContract(blockNumber: BigInteger, hash: String, contract: Contract): Transfer? {
        val params = contract.parameter.value
        val data = params.data ?: return null
        if (!data.startsWith(TRC20_TRANSFER_METHOD_SIG)) return null
        val toAddress = data.substring(30, 72).asTronAddress()
        val amount = runCatching { BigInteger(data.substring(72), 16).toBigDecimal() }.getOrElse { BigDecimal.ZERO }
        return Transfer(
            hash,
            blockNumber,
            Wallet(toAddress),
            true,
            amount,
            params.contractAddress?.asTronAddress()!!,
            chainName
        )
    }

    private fun handleTransferContract(blockNumber: BigInteger, hash: String, contract: Contract): Transfer {
        return with(contract.parameter.value) {
            Transfer(
                hash,
                blockNumber,
                Wallet(to?.asTronAddress()!!),
                false,
                amount?.toBigDecimal()!!,
                chainName,
            )
        }
    }

    override suspend fun decode(input: BlockResponse): List<Transfer> {
        val blockNumber = input.blockHeader?.rawData?.number?.toBigInteger()
            ?: throw java.lang.IllegalArgumentException("Block number must not be null")
        return input.transactions.flatMap { res ->
            res.rawData.contract.mapNotNull {
                when (it.type) {
                    TRIGGER_SMART_CONTRACT -> handleTriggerContract(blockNumber, res.txID, it)
                    TRANSFER_CONTRACT -> handleTransferContract(blockNumber, res.txID, it)
                    else -> null
                }
            }
        }
    }
}
