package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.api.WebhookCaller
import co.nilin.opex.chainscan.scheduler.po.Transfer
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.net.URI

class WebhookCallerImpl(private val webClient: WebClient) : WebhookCaller {
    override suspend fun callWebhook(url: String, data: List<Transfer>) {
        webClient.post()
            .uri(URI.create(url))
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono<Void>()
            .awaitFirst()
    }
}
