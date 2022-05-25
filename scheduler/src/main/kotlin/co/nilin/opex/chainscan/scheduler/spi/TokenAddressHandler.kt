package co.nilin.opex.chainscan.scheduler.spi

import co.nilin.opex.chainscan.scheduler.model.TokenAddressModel

interface TokenAddressHandler {
    suspend fun addTokenAddress(symbol: String, chainName: String, address: String, memo: String?)
    suspend fun deleteTokenAddress(symbol: String)
    suspend fun findTokenAddresses(chainName: String): List<TokenAddressModel>
}
