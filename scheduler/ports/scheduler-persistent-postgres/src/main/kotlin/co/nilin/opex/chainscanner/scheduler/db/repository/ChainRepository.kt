package co.nilin.opex.chainscanner.scheduler.db.repository

import co.nilin.opex.chainscanner.scheduler.db.model.ChainModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChainRepository : ReactiveCrudRepository<ChainModel, Long>
