package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.ChainSyncRecordHandler
import co.nilin.opex.chainscan.scheduler.api.ChainSyncSchedulerHandler
import co.nilin.opex.chainscan.scheduler.api.WebhookCaller
import co.nilin.opex.chainscan.scheduler.po.TransferResult
import co.nilin.opex.chainscan.scheduler.sample.VALID
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.math.BigInteger
import java.net.URI
import java.time.LocalDateTime

inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

@Service
class ScheduleServiceTest {
    private val chainSyncRecordHandler: ChainSyncRecordHandler = mockk()
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler = mockk()
    private val webClient: WebClient = mockk()
    private val webhookCaller: WebhookCaller = mockk()
    private val webhook: String = "http://bc-gateway"
    private val scheduleService: ScheduleService =
        ScheduleService(chainSyncRecordHandler, chainSyncSchedulerHandler, webClient, webhookCaller, webhook)

    @Test
    fun givenSchedule_whenRunSync_thenSuccess(): Unit = runBlocking {
        mockkStatic(LocalDateTime::class)
        every { LocalDateTime.now() } returns VALID.CURRENT_LOCAL_DATE_TIME
        every { webClient.post() } returns mockk {
            every { uri(any<URI>()) } returns mockk {
                every { retrieve() } returns mockk {
                    every { onStatus(any(), any()) } returns mockk {
                        every {
                            bodyToMono(typeRef<TransferResult>())
                        } returns Mono.just(TransferResult(BigInteger.ZERO, BigInteger.ZERO, emptyList()))
                    }
                }
            }
        }
        coEvery {
            webhookCaller.callWebhook(webhook, mapOf(VALID.SCHEDULE.chainName to emptyList()))
        } returns Unit
        coEvery {
            chainSyncRecordHandler.lastSyncRecord(VALID.SCHEDULE.chainName)
        } returns null
        coEvery {
            chainSyncRecordHandler.saveSyncRecord(VALID.CHAIN_SYNC_RECORD)
        } returns Unit
        coEvery {
            chainSyncSchedulerHandler.fetchActiveSchedules(VALID.CURRENT_LOCAL_DATE_TIME)
        } returns listOf(VALID.SCHEDULE)
        scheduleService.start()
    }
}
