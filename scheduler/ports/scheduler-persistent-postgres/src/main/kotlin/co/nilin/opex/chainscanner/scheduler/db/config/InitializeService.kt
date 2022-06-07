package co.nilin.opex.chainscanner.scheduler.db.config

import co.nilin.opex.chainscanner.preferences.Chain
import co.nilin.opex.chainscanner.preferences.Preferences
import co.nilin.opex.chainscanner.scheduler.db.model.ChainScannerModel
import co.nilin.opex.chainscanner.scheduler.db.model.ChainSyncScheduleModel
import co.nilin.opex.chainscanner.scheduler.db.repository.ChainRepository
import co.nilin.opex.chainscanner.scheduler.db.repository.ChainScannerRepository
import co.nilin.opex.chainscanner.scheduler.db.repository.ChainSyncScheduleRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.annotation.PostConstruct

@Component
@DependsOn("postgresConfig")
class InitializeService(
    private val chainRepository: ChainRepository,
    private val chainSyncScheduleRepository: ChainSyncScheduleRepository,
    private val chainScannerRepository: ChainScannerRepository
) {
    @Autowired
    private lateinit var preferences: Preferences

    @PostConstruct
    fun init() = runBlocking {
        addChains(preferences.chains)
        addSchedules(preferences)
    }

    private suspend fun addChains(data: List<Chain>) = coroutineScope {
        data.map { chainRepository.insert(it.name).awaitSingleOrNull() }
    }

    private suspend fun addSchedules(data: Preferences) = coroutineScope {
        data.chains.map {
            ChainSyncScheduleModel(
                it.name,
                LocalDateTime.now(),
                it.schedule.delay,
                it.schedule.errorDelay,
                it.schedule.timeout.toLong(),
                true,
                it.schedule.confirmations,
                it.schedule.maxRetries
            )
        }.also {
            runCatching { chainSyncScheduleRepository.saveAll(it).collectList().awaitSingle() }
        }
        data.chains.flatMap { c ->
            c.scanners.map { s ->
                ChainScannerModel(
                    c.name,
                    s.url,
                    s.maxBlockRange,
                    s.delayOnRateLimit
                )
            }
        }.also {
            runCatching { chainScannerRepository.saveAll(it).collectList().awaitSingle() }
        }
    }
}
