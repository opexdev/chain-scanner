package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.api.ChainSyncSchedulerHandler
import co.nilin.opex.chainscan.scheduler.dto.toPlainObject
import co.nilin.opex.chainscan.scheduler.model.ChainSyncScheduleModel
import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule
import co.nilin.opex.chainscan.scheduler.repository.ChainSyncScheduleRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class ChainSyncSchedulerHandlerImpl(private val chainSyncScheduleRepository: ChainSyncScheduleRepository) :
    ChainSyncSchedulerHandler {
    override suspend fun fetchActiveSchedules(time: LocalDateTime): List<ChainSyncSchedule> {
        return chainSyncScheduleRepository.findActiveSchedule(time).map { it.toPlainObject() }.toList()
    }

    override suspend fun prepareScheduleForNextTry(syncSchedule: ChainSyncSchedule, success: Boolean) {
        val chain = syncSchedule.chainName
        val time = LocalDateTime.now().plus(
            if (success) syncSchedule.delay else syncSchedule.errorDelay,
            ChronoUnit.SECONDS
        )
        val dao = ChainSyncScheduleModel(chain, time, syncSchedule.delay, syncSchedule.errorDelay)
        chainSyncScheduleRepository.save(dao).awaitFirst()
    }

    override suspend fun scheduleChain(chain: String, delaySeconds: Long, errorDelaySeconds: Long) {
        val doc = chainSyncScheduleRepository.findAll().awaitFirstOrNull()
            ?.copy(delay = delaySeconds, errorDelay = errorDelaySeconds)
            ?: ChainSyncScheduleModel(chain, LocalDateTime.now(), delaySeconds, errorDelaySeconds)
        chainSyncScheduleRepository.save(doc).awaitFirstOrNull()
    }
}
