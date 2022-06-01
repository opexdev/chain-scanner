package co.nilin.opex.chainscan.database.dao

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("watch_list")
data class WatchListItemModel(
    val address: String,
    @Id val id: Long? = null
)
