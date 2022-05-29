package co.nilin.opex.chainscan.database.repository

import co.nilin.opex.chainscan.database.model.ChainEndpointModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChainEndpointRepository : ReactiveCrudRepository<ChainEndpointModel, Long>
