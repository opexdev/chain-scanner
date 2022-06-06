package co.nilin.opex.chainscanner.scheduler.service

import co.nilin.opex.chainscanner.scheduler.core.po.Transfer
import co.nilin.opex.chainscanner.scheduler.core.spi.WebhookCaller
import co.nilin.opex.chainscanner.scheduler.exceptions.WebhookException
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
        val uri = URI.create("$onSyncWebhookUrl/$chainName").normalize()
        runCatching {
            webClient.put()
                .uri(uri)
                .retrieve()
                .onStatus({ t -> t.isError }, { it.createException() })
                .bodyToMono<Void>()
                .awaitFirst()
        }.onFailure {
            rethrowWebhookExceptions(it, uri.toString())
        }.onSuccess {
            logger.debug("Successfully sent transfers url: $uri count: ${data.size}")
        }
    }

    private fun rethrowWebhookExceptions(e: Throwable, uri: String): Nothing = when (e) {
        is WebClientRequestException ->
            throw WebhookException("Failed to call webhook url: `$uri` error: ${e.cause?.message ?: e.message}")
        is WebClientResponseException ->
            throw WebhookException("Failed to call webhook url: `$uri` error: ${e.statusCode.name}(${e.statusCode.value()}")
        else -> throw e
    }
}
