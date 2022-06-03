package co.nilin.opex.chainscanner.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("watch_list")
data class WatchListItemModel(
    val symbol: String,
    val name: String,
    val address: String,
    @Id val id: Long? = null
)
