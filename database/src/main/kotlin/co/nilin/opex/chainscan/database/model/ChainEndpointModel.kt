package co.nilin.opex.chainscan.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("chain_endpoints")
data class ChainEndpointModel(
    val endpoint_url: String,
    val apiKey: String?,
    val requestPerSec: Int = 0,
    @Id var id: Long? = null
)
