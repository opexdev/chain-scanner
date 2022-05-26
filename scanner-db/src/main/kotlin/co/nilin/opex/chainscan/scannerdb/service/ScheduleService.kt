package co.nilin.opex.chainscan.scannerdb.service

import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduleService(private val chainSyncService: ChainSyncService) {
    @Scheduled(fixedDelay = 1000)
    fun start() = runBlocking {
        chainSyncService.startSyncWithChain()
    }
}
