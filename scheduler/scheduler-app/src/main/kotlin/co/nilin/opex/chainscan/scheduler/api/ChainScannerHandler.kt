package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.ChainScanner

interface ChainScannerHandler {
    suspend fun getScannersByName(chainName: String): List<ChainScanner>
}