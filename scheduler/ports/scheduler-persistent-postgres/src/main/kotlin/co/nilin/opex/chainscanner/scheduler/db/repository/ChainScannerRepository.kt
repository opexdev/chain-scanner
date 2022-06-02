package co.nilin.opex.chainscanner.scheduler.db.repository

import co.nilin.opex.chainscanner.scheduler.db.model.ChainScannerModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ChainScannerRepository : ReactiveCrudRepository<ChainScannerModel, Long> {
    fun findByChainName(chainName: String): Flux<ChainScannerModel>
}
