package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.Transfer
import java.math.BigInteger

interface TransferCacheHandler {
    suspend fun saveTransfers(transfers: List<Transfer>)
    suspend fun getTransfers(tokenAddresses: List<String>): List<Transfer>
    suspend fun clearCache(blockNumber: BigInteger)
}
