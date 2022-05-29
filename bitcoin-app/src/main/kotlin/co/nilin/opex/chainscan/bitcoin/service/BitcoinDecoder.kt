package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.bitcoin.data.BlockResponse
import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.Wallet
import co.nilin.opex.chainscan.core.spi.Decoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class BitcoinDecoder(@Value("\${app.chain-name}") private val chainName: String) : Decoder<BlockResponse> {
    override fun invoke(input: BlockResponse): List<Transfer> {
        return input.tx.flatMap { tx ->
            tx.vout.map { v ->
                Transfer(
                    "${tx.hash}_${v.scriptPubKey?.hex}",
                    input.height.toBigInteger(),
                    Wallet(""),
                    Wallet(v.scriptPubKey?.address!!),
                    false,
                    v.value,
                    chainName
                )
            }
        }
    }
}
