package co.nilin.opex.chainscan.database.impl

import co.nilin.opex.chainscan.core.model.WatchList
import co.nilin.opex.chainscan.core.spi.WatchListHandler
import co.nilin.opex.chainscan.database.dto.toModel
import co.nilin.opex.chainscan.database.dto.toPlainObject
import co.nilin.opex.chainscan.database.repository.WatchListRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component

@Component
class WatchListHandlerImpl(private val watchListRepository: WatchListRepository) : WatchListHandler {
    override suspend fun add(watchList: WatchList) {
        watchListRepository.save(watchList.toModel()).awaitSingle()
    }

    override suspend fun deleteByAddress(address: String) {
        watchListRepository.deleteByAddress(address).awaitSingle()
    }

    override suspend fun findAll(): List<WatchList> {
        return watchListRepository.findAll().collectList().awaitSingle().map { it.toPlainObject() }
    }
}
