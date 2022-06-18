package co.nilin.opex.chainscanner.scheduler.core.spi

import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner

interface ChainScannerHandler {
    suspend fun getScannersByName(chainName: String): List<ChainScanner>
}
