package co.nilin.opex.chainscan.gateway.repository

import co.nilin.opex.chainscan.gateway.model.ScannerModule
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ScannerRepository : ReactiveCrudRepository<ScannerModule, Long>