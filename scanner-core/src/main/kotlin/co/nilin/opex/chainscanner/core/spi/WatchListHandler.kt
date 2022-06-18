package co.nilin.opex.chainscanner.core.spi

import co.nilin.opex.chainscanner.core.model.WatchListItem

interface WatchListHandler {
    suspend fun add(watchListItem: WatchListItem)
    suspend fun deleteByAddress(address: String)
    suspend fun findAll(): List<WatchListItem>
}
