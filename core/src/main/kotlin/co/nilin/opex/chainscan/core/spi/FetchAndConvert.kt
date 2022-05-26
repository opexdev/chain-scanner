package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.DepositResult
import co.nilin.opex.chainscan.core.model.TransfersRequest

interface FetchAndConvert {
    suspend fun fetchAndConvert(endpoint: String, request: TransfersRequest): DepositResult
}
