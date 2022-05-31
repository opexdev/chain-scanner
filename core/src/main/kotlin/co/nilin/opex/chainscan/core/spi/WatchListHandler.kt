package co.nilin.opex.chainscan.core.spi

import co.nilin.opex.chainscan.core.model.WatchList

interface WatchListHandler {
    suspend fun add(watchList: WatchList)
    suspend fun deleteByAddress(address: String)
    suspend fun findAll(): List<WatchList>
}
