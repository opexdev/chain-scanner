package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import java.math.BigInteger

interface ChainEndpointProxy {
    data class DepositFilter(val startBlock: BigInteger?, val endBlock: BigInteger?, val tokenAddresses: List<String>?)

    suspend fun syncTransfers(filter: DepositFilter): ChainSyncRecord
}
