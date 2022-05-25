package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.spi.WebhookCaller
import org.slf4j.LoggerFactory

class WebhookCallerImpl : WebhookCaller {
    private val logger = LoggerFactory.getLogger(ChainEndpointProxyImpl::class.java)

    override suspend fun callWebhook(url: String, data: Any) {
        logger.info("Trigger {}", url)
    }
}
