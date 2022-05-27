package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.ChainSyncSchedulerHandler
import co.nilin.opex.chainscan.scheduler.po.TransferResult
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import org.springframework.core.ParameterizedTypeReference
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI
import java.time.LocalDateTime

inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

@Service
class ScheduleService(
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    private val webClient: WebClient
) {
    @Scheduled(fixedDelay = 1000)
    fun start(): Nothing = runBlocking {
        val schedules = chainSyncSchedulerHandler.fetchActiveSchedules(LocalDateTime.now())
        val map = schedules.associate {
            val response = webClient.post()
                .uri(URI.create(it.chainName))
                .retrieve()
                .onStatus({ t -> t.isError }, { it.createException() })
                .bodyToMono(typeRef<TransferResult>())
                .awaitFirst()
            it.chainName to response
        }
        TODO("Call webhook")
        TODO("Update sync records")
    }
}
