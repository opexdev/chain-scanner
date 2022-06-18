package co.nilin.opex.chainscanner.scheduler.db.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("chains")
data class ChainModel(
    @Id val name: String
)
