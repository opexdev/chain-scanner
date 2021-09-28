package co.nilin.opex.chainscan.gateway.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("scanner_module")
data class ScannerModule(
    @Id
    val id: Long? = null,
    val name: String,
    val url: String
)