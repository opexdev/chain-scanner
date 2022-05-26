package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.TransfersResult

interface ChainTransferHandler {
    suspend fun getTransfers(startBlock: Long, endBlock: Long?, addresses: List<String>?): TransfersResult
}
