package co.nilin.opex.chainscan.scheduler.db.dao

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("chains")
data class ChainModel(
    @Id val name: String
)
