package co.nilin.opex.chainscan.scheduler.core.spi

import co.nilin.opex.chainscan.scheduler.core.po.ChainScanner

interface ChainScannerHandler {
    suspend fun getScannersByName(chainName: String): List<ChainScanner>
}
