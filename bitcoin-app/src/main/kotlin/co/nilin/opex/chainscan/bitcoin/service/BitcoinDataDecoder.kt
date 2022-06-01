package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.bitcoin.data.BlockResponse
import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.Wallet
import co.nilin.opex.chainscan.core.spi.DataDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class BitcoinDataDecoder(@Value("\${app.chain.name}") private val chainName: String) : DataDecoder<BlockResponse> {
    override suspend fun decode(input: BlockResponse): List<Transfer> {
        return input.tx.flatMap { tx ->
            tx.vout.asFlow().flowOn(Dispatchers.IO).filter {
                !it.scriptPubKey?.address.isNullOrBlank()
            }.map { v ->
                Transfer(
                    "${tx.hash}_${v.n}",
                    input.height.toBigInteger(),
                    Wallet(v.scriptPubKey!!.address!!),
                    false,
                    v.value,
                    chainName
                )
            }.buffer().toList()
        }
    }
}
