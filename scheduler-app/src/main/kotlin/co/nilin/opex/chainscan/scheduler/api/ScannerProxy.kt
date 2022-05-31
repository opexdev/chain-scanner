package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.TransferResult
import java.math.BigInteger

interface ScannerProxy {
    suspend fun getTransfers(url: String, blockNumber: BigInteger? = null): TransferResult
    suspend fun getBlockNumber(url: String): BigInteger
}