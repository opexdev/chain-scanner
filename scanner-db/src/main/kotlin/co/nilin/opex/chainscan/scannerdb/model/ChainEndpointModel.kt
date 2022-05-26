package co.nilin.opex.chainscan.scannerdb.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("chain_endpoints")
data class ChainEndpointModel(
    @Column("endpoint_url") val url: String,
    @Column("endpoint_user") val user: String?,
    @Column("endpoint_password") val password: String?,
    @Id var id: Long? = null
)
