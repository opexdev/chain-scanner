package co.nilin.opex.chainscan.scannerdb.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("watch_list")
data class WatchListModel(
    val address: String,
    @Id val id: Long? = null
)
