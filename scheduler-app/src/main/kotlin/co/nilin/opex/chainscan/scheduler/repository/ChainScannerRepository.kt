package co.nilin.opex.chainscan.scheduler.repository

import co.nilin.opex.chainscan.scheduler.model.ChainScannerModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ChainScannerRepository : ReactiveCrudRepository<ChainScannerModel, Long> {
    fun findByChainName(chainName: String): Flux<ChainScannerModel>
}
