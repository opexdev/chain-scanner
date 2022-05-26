package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.spi.TransferCacheHandler
import java.math.BigInteger

class TransferCacheHandlerImpl : TransferCacheHandler {
    override suspend fun saveTransfers(transfers: List<Transfer>) {
        TODO("Not yet implemented")
    }

    override suspend fun getTransfers(tokenAddresses: List<String>): List<Transfer> {
        TODO("Not yet implemented")
    }

    override suspend fun clearCache(blockNumber: BigInteger) {
        TODO("Not yet implemented")
    }
}
