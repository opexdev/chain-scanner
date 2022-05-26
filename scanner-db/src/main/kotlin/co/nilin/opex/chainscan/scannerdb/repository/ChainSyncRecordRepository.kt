package co.nilin.opex.chainscan.scannerdb.repository

import co.nilin.opex.chainscan.scannerdb.model.ChainSyncRecordModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChainSyncRecordRepository : ReactiveCrudRepository<ChainSyncRecordModel, Long>
