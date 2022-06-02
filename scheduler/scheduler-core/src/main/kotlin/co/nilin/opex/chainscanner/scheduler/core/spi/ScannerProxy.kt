package co.nilin.opex.chainscanner.scheduler.core.spi

import co.nilin.opex.chainscanner.scheduler.core.po.Transfer
import java.math.BigInteger

interface ScannerProxy {
    suspend fun getTransfers(url: String, blockNumber: BigInteger? = null): List<Transfer>
    suspend fun getBlockNumber(url: String): BigInteger
}
