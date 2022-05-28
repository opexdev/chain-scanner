package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.TransferResult

interface ScannerProxy {
    suspend fun getTransfers(url: String): TransferResult
}