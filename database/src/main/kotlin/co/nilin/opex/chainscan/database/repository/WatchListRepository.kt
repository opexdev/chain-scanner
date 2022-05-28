package co.nilin.opex.chainscan.database.repository

import co.nilin.opex.chainscan.database.model.WatchListModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WatchListRepository : ReactiveCrudRepository<WatchListModel, Long> {
    fun deleteByAddress(address: String): Mono<Int>
}
