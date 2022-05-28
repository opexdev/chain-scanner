package co.nilin.opex.chainscan.database.impl

import co.nilin.opex.chainscan.core.model.WatchList
import co.nilin.opex.chainscan.core.spi.WatchListHandler
import co.nilin.opex.chainscan.database.model.WatchListModel
import co.nilin.opex.chainscan.database.repository.WatchListRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component

@Component
class WatchListHandlerImpl(private val watchListRepository: WatchListRepository) : WatchListHandler {
    override suspend fun add(address: String) {
        watchListRepository.save(WatchListModel(address)).awaitSingleOrNull()
    }

    override suspend fun deleteByAddress(address: String) {
        watchListRepository.deleteByAddress(address).awaitFirstOrNull()
    }

    override suspend fun findAll(): List<WatchList> {
        return watchListRepository.findAll().collectList().awaitSingle().map { WatchList(it.address) }
    }
}
