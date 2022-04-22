package co.nilin.opex.chainscan.tron.service

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.TransfersResult
import co.nilin.opex.chainscan.core.spi.Chain
import co.nilin.opex.chainscan.tron.data.TransactionResponse
import co.nilin.opex.chainscan.tron.proxy.TronGridProxy
import co.nilin.opex.chainscan.tron.utils.tryOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RestChainService(private val proxy: TronGridProxy, private val interpreter: TronInterpreter) : Chain {

    private val logger = LoggerFactory.getLogger(RestChainService::class.java)

    override suspend fun getTransfers(startBlock: Long, endBlock: Long?, addresses: List<String>?): TransfersResult {
        logger.info("Requested blocks: startBlock=$startBlock, endBlock=$endBlock")

        var last = endBlock ?: (startBlock + 10)
        val first = if (startBlock == 0L || startBlock > last || last - startBlock > 10) last - 10 else startBlock
        val transfers = mutableListOf<Transfer>()

        logger.info("Start fetching tron transfers: startBlock=$first, endBlock=$last")
        coroutineScope {
            tryOrNull { proxy.getLatestBlock() }?.let {
                val blockNumber = it.blockHeader?.rawData?.number
                if (blockNumber != null && last > blockNumber)
                    last = blockNumber
            }

            val transactions = ArrayList<TransactionResponse>()
            for (i in first until last + 1) {
                tryOrNull { proxy.getBlockByNumber(i) }?.let { d -> transactions.addAll(d.transactions) }
            }

            transactions.forEach { tx ->
                launch(Dispatchers.IO) {
                    interpreter.interpret(tx).onEach {
                        if (!it.isTokenTransfer || addresses?.contains(it.token ?: "") == true)
                            transfers.add(it)
                    }
                }
            }
        }

        logger.info("Finished fetching transactions: lastBlock=$last transfers=${transfers.size}")
        return TransfersResult(last, transfers)
    }
}