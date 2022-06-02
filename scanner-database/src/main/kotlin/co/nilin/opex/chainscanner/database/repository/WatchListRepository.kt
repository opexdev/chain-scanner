package co.nilin.opex.chainscanner.database.repository

import co.nilin.opex.chainscanner.database.model.WatchListItemModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WatchListRepository : ReactiveCrudRepository<WatchListItemModel, Long> {
    fun deleteByAddress(address: String): Mono<Int>
}
