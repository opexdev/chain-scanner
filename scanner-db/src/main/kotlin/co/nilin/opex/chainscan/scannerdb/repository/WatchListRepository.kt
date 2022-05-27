package co.nilin.opex.chainscan.scannerdb.repository

import co.nilin.opex.chainscan.scannerdb.model.WatchListModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WatchListRepository : ReactiveCrudRepository<WatchListModel, Long> {
    fun deleteByAddress(address: String): Mono<Int>
}
