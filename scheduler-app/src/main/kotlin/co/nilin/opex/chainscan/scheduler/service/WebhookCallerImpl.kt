package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.WebhookCaller
import co.nilin.opex.chainscan.scheduler.po.Transfer
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import java.net.URI

@Service
class WebhookCallerImpl(private val webClient: WebClient) : WebhookCaller {
    private val logger: Logger by LoggerDelegate()

    override suspend fun callWebhook(url: String, data: List<Transfer>) {
        runCatching {
            webClient.post()
                .uri(URI.create(url))
                .retrieve()
                .onStatus({ t -> t.isError }, { it.createException() })
                .bodyToMono<Void>()
                .awaitFirst()
        }.onSuccess {
            logger.trace("Successfully sent ${data.size} transfers to $url")
        }.onFailure { e ->
            logger.trace("Error occurred for url: $url")
            (e as? WebClientResponseException)?.let {
                logger.trace("Failed to send ${data.size} transfers to `$url`: ${it.statusCode.name}(${it.statusCode.value()})")
            }
        }
    }
}