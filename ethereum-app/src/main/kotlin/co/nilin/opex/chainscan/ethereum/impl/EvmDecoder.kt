package co.nilin.opex.chainscan.ethereum.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.Wallet
import co.nilin.opex.chainscan.core.spi.Decoder
import co.nilin.opex.chainscan.ethereum.utils.checksumAddress
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.web3j.protocol.core.methods.response.EthBlock
import java.math.BigDecimal
import java.math.BigInteger

const val ERC20_TRANSFER_METHOD_SIG = "0xa9059cbb000000000000000000000000"
const val ETH_TRANSFER_METHOD_SIG = "0x"

@Component
class EvmDecoder(@Value("\${app.chain-name}") private val chainName: String) :
    Decoder<EthBlock.TransactionObject> {
    private fun isAssetTransfer(input: String) = input == ETH_TRANSFER_METHOD_SIG
    private fun isTokenTransfer(input: String) = input.length == 138 && input.startsWith(ERC20_TRANSFER_METHOD_SIG)

    private fun decodeAssetTransfer(tx: EthBlock.TransactionObject): Transfer {
        require(isAssetTransfer(tx.input))
        val amount = runCatching { tx.value.toBigDecimal() }.getOrElse { BigDecimal.ZERO }
        return Transfer(
            tx.hash,
            tx.blockNumber,
            Wallet(tx.to.checksumAddress()),
            false,
            amount,
            chainName
        )
    }

    private fun decodeTokenTransfer(tx: EthBlock.TransactionObject): Transfer {
        require(isTokenTransfer(tx.input))
        val address = runCatching { "0x${tx.input.substring(34, 74)}" }.getOrElse { "0x" }.checksumAddress()
        val receiver = Wallet(address)
        val amount = runCatching { BigInteger(tx.input.substring(74), 16).toBigDecimal() }.getOrElse { BigDecimal.ZERO }
        return Transfer(
            tx.hash,
            tx.blockNumber,
            receiver,
            true,
            amount,
            chainName,
            tx.to.checksumAddress(),
        )
    }

    override fun invoke(input: EthBlock.TransactionObject): List<Transfer> = when {
        isAssetTransfer(input.input) -> listOf(decodeAssetTransfer(input))
        isTokenTransfer(input.input) -> listOf(decodeTokenTransfer(input))
        else -> emptyList()
    }
}
