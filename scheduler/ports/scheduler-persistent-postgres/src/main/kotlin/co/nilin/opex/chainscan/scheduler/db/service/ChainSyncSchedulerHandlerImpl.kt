package co.nilin.opex.chainscan.scheduler.db.service

import co.nilin.opex.chainscan.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscan.scheduler.db.dao.ChainSyncScheduleModel
import co.nilin.opex.chainscan.scheduler.db.dto.toModel
import co.nilin.opex.chainscan.scheduler.db.dto.toPlainObject
import co.nilin.opex.chainscan.scheduler.db.repository.ChainSyncScheduleRepository
import co.nilin.opex.chainscan.scheduler.core.po.ChainSyncSchedule
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ChainSyncSchedulerHandlerImpl(private val chainSyncScheduleRepository: ChainSyncScheduleRepository) :
    ChainSyncSchedulerHandler {
    override suspend fun fetchActiveSchedules(time: LocalDateTime): List<ChainSyncSchedule> {
        return chainSyncScheduleRepository.findActiveSchedule(time).map { it.toPlainObject() }.toList()
    }

    override suspend fun save(syncSchedule: ChainSyncSchedule) {
        chainSyncScheduleRepository.save(syncSchedule.toModel()).awaitFirst()
    }

    override suspend fun scheduleChain(chain: String, delaySeconds: Long, errorDelaySeconds: Long) {
        val doc = chainSyncScheduleRepository.findAll().awaitFirstOrNull()
            ?.copy(delay = delaySeconds, errorDelay = errorDelaySeconds)
            ?: ChainSyncScheduleModel(chain, LocalDateTime.now(), delaySeconds, errorDelaySeconds)
        chainSyncScheduleRepository.save(doc).awaitFirstOrNull()
    }
}
