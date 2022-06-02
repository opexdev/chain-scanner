package co.nilin.opex.chainscanner.scheduler.db.service

import co.nilin.opex.chainscanner.scheduler.core.po.ChainSyncSchedule
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.db.dto.toModel
import co.nilin.opex.chainscanner.scheduler.db.dto.toPlainObject
import co.nilin.opex.chainscanner.scheduler.db.model.ChainSyncScheduleModel
import co.nilin.opex.chainscanner.scheduler.db.repository.ChainSyncScheduleRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ChainSyncSchedulerHandlerImpl(private val chainSyncScheduleRepository: ChainSyncScheduleRepository) :
    ChainSyncSchedulerHandler {
    override suspend fun fetchActiveSchedules(dateTime: LocalDateTime): List<ChainSyncSchedule> {
        return chainSyncScheduleRepository.findActiveSchedule(dateTime).map { it.toPlainObject() }.toList()
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
