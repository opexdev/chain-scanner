package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.Endpoint

interface ChainEndpointHandler {
    suspend fun addEndpoint(url: String, apiKey: String?)
    suspend fun deleteEndpoint(id: Long)
    suspend fun findAll(): List<Endpoint>
}
