package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.bitcoin.GetBlockProxy
import co.nilin.opex.chainscan.bitcoin.data.TransactionResponse
import co.nilin.opex.chainscan.bitcoin.utils.justTry
import co.nilin.opex.chainscan.bitcoin.utils.justTryOrNull
import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.TransfersResult
import co.nilin.opex.chainscan.core.spi.Chain
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class RestChainService(private val proxy: GetBlockProxy) : Chain {

    override suspend fun getTransfers(startBlock: Long, endBlock: Long?, addresses: List<String>?): TransfersResult {
        val blockHash = ArrayList<String?>()
        val last = endBlock ?: proxy.getInfo()?.blocks ?: (startBlock + 10)

        for (i in startBlock until last + 1) {
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
            tx.vout.forEach {
                transfers.add(
                    Transfer(
                        tx.hash,
                        "",
                        it.scriptPubKey?.address ?: "",
                        false,
                        null,
                        it.value
                    )
                )
            }
        }

        return TransfersResult(last, transfers)
    }
}