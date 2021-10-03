package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.bitcoin.GetBlockProxy
import co.nilin.opex.chainscan.bitcoin.data.TransactionResponse
import co.nilin.opex.chainscan.bitcoin.utils.justTry
import co.nilin.opex.chainscan.bitcoin.utils.justTryOrNull
import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.Chain
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class RestChainService(private val proxy: GetBlockProxy) : Chain {

    override suspend fun getTransfers(startBlock: Long, endBlock: Long, addresses: List<String>?): List<Transfer> {
        val blockHash = ArrayList<String?>()
        for (i in startBlock until endBlock + 1) {
            justTry { blockHash.add(proxy.getBlockHash(i)) }
        }

        val transactions = ArrayList<TransactionResponse>()
        blockHash.forEach {
            if (!it.isNullOrEmpty()) {
                val data = justTryOrNull { proxy.getBlockData(it) }
                data?.let { d ->
                    transactions.addAll(d.tx)
                }
            }
        }

        val transfers = ArrayList<Transfer>()
        transactions.forEach { tx ->
            val s = tx.vout.find { addresses?.contains(it.scriptPubKey?.address) == true }
            if (s != null)
                transfers.add(
                    Transfer(
                        tx.hash,
                        "",
                        s.scriptPubKey?.address ?: "",
                        false,
                        null,
                        s.value
                    )
                )
        }

        return transfers
    }
}