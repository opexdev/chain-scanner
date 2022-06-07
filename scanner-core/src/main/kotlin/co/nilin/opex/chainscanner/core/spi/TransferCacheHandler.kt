package co.nilin.opex.chainscanner.core.spi

import co.nilin.opex.chainscanner.core.model.Transfer
import java.math.BigInteger

interface TransferCacheHandler {
    suspend fun saveTransfers(transfers: List<Transfer>)
    suspend fun getTransfers(tokenAddresses: List<String>, blockNumber: BigInteger? = null): List<Transfer>
    suspend fun clearCache(blockNumber: BigInteger)
}
