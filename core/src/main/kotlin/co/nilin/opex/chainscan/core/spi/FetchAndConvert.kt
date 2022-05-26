package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.Transfer
import java.math.BigInteger

interface FetchAndConvert {
    suspend fun fetchAndConvert(
        endpoint: String,
        startBlock: BigInteger?,
        endBlock: BigInteger?,
        tokenAddresses: List<String> = emptyList()
    ): List<Transfer>
}
