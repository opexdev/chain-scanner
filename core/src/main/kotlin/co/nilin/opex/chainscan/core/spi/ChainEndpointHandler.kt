package co.nilin.opex.chainscan.core.spi

interface ChainEndpointHandler {
    suspend fun addEndpoint(url: String, username: String?, password: String?)
    suspend fun deleteEndpoint(url: String)
    suspend fun findChainEndpointProxy(): ChainEndpointProxy
}
