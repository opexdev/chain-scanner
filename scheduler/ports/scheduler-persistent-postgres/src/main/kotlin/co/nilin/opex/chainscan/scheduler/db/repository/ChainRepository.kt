package co.nilin.opex.chainscan.scheduler.db.repository

import co.nilin.opex.chainscan.scheduler.db.model.ChainModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChainRepository : ReactiveCrudRepository<ChainModel, Long>
