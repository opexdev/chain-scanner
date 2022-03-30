package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.TransfersResult
import co.nilin.opex.chainscan.core.spi.Chain
import co.nilin.opex.chainscan.tron.data.TransactionResponse
import co.nilin.opex.chainscan.tron.proxy.TronGridProxy
import co.nilin.opex.chainscan.tron.utils.justTryOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class RestChainService(private val proxy: TronGridProxy) : Chain {

    override suspend fun getTransfers(startBlock: Long, endBlock: Long?, addresses: List<String>?): TransfersResult {
        val last = endBlock ?: (startBlock + 10)
        val first = if (startBlock == 0L || startBlock >= last) last - 3 else startBlock

        val transactions = ArrayList<TransactionResponse>()
        for (i in first until last + 1) {
            val data = justTryOrNull { proxy.getBlockByNumber(i) }
            data?.let { d ->
                transactions.addAll(d.transactions)
            }
        }

        val transfers = ArrayList<Transfer>()
        transactions.forEach { tx ->
            if (tx.isTransfer()) {
                transfers.add(
                    Transfer(
                        tx.txID,
                        tx.from(),
                        tx.to(),
                        false,
                        null,
                        tx.amount()?.toBigDecimal() ?: BigDecimal.ZERO
                    )
                )
            }
        }

        return TransfersResult(last, transfers)
    }
}