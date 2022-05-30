package co.nilin.opex.chainscan.scheduler.repository

import co.nilin.opex.chainscan.scheduler.model.ChainSyncRetryModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChainSyncRetryRepository : ReactiveCrudRepository<ChainSyncRetryModel, Long>
