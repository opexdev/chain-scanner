package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.TokenAddress

interface TokenAddressHandler {
    suspend fun addTokenAddress(symbol: String, chainName: String, address: String, memo: String?)
    suspend fun deleteTokenAddress(symbol: String)
    suspend fun findTokenAddresses(chainName: String): List<TokenAddress>
}
