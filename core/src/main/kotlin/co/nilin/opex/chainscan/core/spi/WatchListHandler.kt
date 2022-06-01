package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.WatchListItem

interface WatchListHandler {
    suspend fun add(watchListItem: WatchListItem)
    suspend fun deleteByAddress(address: String)
    suspend fun findAll(): List<WatchListItem>
}
