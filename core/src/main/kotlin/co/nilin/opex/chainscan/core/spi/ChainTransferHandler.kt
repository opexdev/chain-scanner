package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.TransfersResult
import java.math.BigInteger

interface ChainTransferHandler {
    suspend fun getTransfers(startBlock: BigInteger?, endBlock: BigInteger?, addresses: List<String>?): TransfersResult
}
