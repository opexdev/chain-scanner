package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.TokenAddress

interface TokenAddressHandler {
    suspend fun addTokenAddress(symbol: String, address: String)
    suspend fun deleteTokenAddress(symbol: String)
    suspend fun findTokenAddresses(): List<TokenAddress>
}
