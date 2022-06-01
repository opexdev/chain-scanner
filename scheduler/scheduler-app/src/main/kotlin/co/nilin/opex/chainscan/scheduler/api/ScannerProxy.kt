package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.Transfer
import java.math.BigInteger

interface ScannerProxy {
    suspend fun getTransfers(url: String, blockNumber: BigInteger? = null): List<Transfer>
    suspend fun getBlockNumber(url: String): BigInteger
}