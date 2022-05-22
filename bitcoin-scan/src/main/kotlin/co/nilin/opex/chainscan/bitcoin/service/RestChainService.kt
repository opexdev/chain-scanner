package co.nilin.opex.chainscan.bitcoin.service

import co.nilin.opex.chainscan.bitcoin.GetBlockProxy
import co.nilin.opex.chainscan.bitcoin.data.TransactionResponse
import co.nilin.opex.chainscan.bitcoin.utils.justTry
import co.nilin.opex.chainscan.bitcoin.utils.justTryOrNull
import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.TransfersResult
import co.nilin.opex.chainscan.core.spi.Chain
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RestChainService(private val proxy: GetBlockProxy) : Chain {

    private val logger = LoggerFactory.getLogger(RestChainService::class.java)

    override suspend fun getTransfers(startBlock: Long, endBlock: Long?, addresses: List<String>?): TransfersResult {
        logger.info("Requested blocks: startBlock=$startBlock, endBlock=$endBlock")

        val blockHash = ArrayList<String?>()

        val networkHeight = proxy.getInfo()?.blocks ?: throw IllegalStateException("Could not fetch latest block")
        val last = if (endBlock == null || endBlock > networkHeight) networkHeight else endBlock
        val first = if (startBlock == 0L || startBlock > last)
            last - 10
        else if (last - startBlock > 50)
            last - 50
        else startBlock

        logger.info("Start fetching bitcoin transfers: startBlock=$first, endBlock=$last")
        for (i in first until last + 1) {
            justTry { blockHash.add(proxy.getBlockHash(i)) }
        }

        val transactions = ArrayList<TransactionResponse>()
        blockHash.forEach {
            if (!it.isNullOrEmpty()) {
                val data = justTryOrNull { proxy.getBlockData(it) }
                data?.let { d ->
                    logger.info("Fetched block ${d.height} with ${d.tx.size} transactions")
                    transactions.addAll(d.tx)
                }
            }
        }

        val transfers = ArrayList<Transfer>()
        transactions.forEach { tx ->
            tx.vout.forEach {
                transfers.add(
                    Transfer(
                        "${tx.hash}_${it.scriptPubKey?.hex}",
                        "",
                        it.scriptPubKey?.address ?: "",
                        false,
                        null,
                        it.value
                    )
                )
            }
        }

        logger.info("Finished fetching transactions: lastBlock=$last transfers=${transfers.size}")
        return TransfersResult(last, transfers)
    }
}