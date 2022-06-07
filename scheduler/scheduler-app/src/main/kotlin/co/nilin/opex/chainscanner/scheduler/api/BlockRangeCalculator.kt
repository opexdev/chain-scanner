package co.nilin.opex.chainscanner.scheduler.api

import co.nilin.opex.chainscanner.scheduler.core.po.ChainScanner

interface BlockRangeCalculator {
    suspend fun calculateBlockRange(chainScanner: ChainScanner, confirmations: Int): LongRange
}