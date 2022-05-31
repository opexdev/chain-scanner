package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.TransferResult
import java.math.BigInteger

interface ScannerProxy {
    suspend fun getTransfers(url: String, startBlock: BigInteger? = null, endBlock: BigInteger? = null): TransferResult
}