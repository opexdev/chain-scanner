package co.nilin.opex.chainscanner.scheduler.service

import co.nilin.opex.chainscanner.scheduler.core.po.Transfer
import co.nilin.opex.chainscanner.scheduler.core.spi.WebhookCaller
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import java.net.URI

@Service
class WebhookCallerImpl(
    private val webClient: WebClient,
    @Value("\${app.on-sync-webhook-url}") private val onSyncWebhookUrl: String
) : WebhookCaller {
    private val logger: Logger by LoggerDelegate()

    override suspend fun callWebhook(chainName: String, data: List<Transfer>) {
        val uri = URI.create(onSyncWebhookUrl).resolve("/$chainName")
        runCatching {
            webClient.put()
                .uri(uri)
                .retrieve()
                .onStatus({ t -> t.isError }, { it.createException() })
                .bodyToMono<Void>()
                .awaitFirst()
        }.onSuccess {
            logger.debug("Successfully sent transfers url: $uri count: ${data.size}")
        }.recoverCatching { e ->
            when (e) {
                is WebClientResponseException -> throw Exception("${e.statusCode.name}(${e.statusCode.value()}")
                is WebClientRequestException -> throw Exception("${e.cause?.message ?: e.message}")
                else -> throw e
            }
        }.onFailure { e ->
            logger.error("Failed to call webhook url: `$uri` error: ${e.cause?.message ?: e.message}")
        }.getOrThrow()
    }
}
