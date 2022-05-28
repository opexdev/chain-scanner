package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.api.WebhookCaller
import co.nilin.opex.chainscan.scheduler.po.Transfer
import org.slf4j.LoggerFactory

class WebhookCallerImpl : WebhookCaller {
    private val logger = LoggerFactory.getLogger(WebhookCallerImpl::class.java)

    override suspend fun callWebhook(url: String, data: Map<String, List<Transfer>>) {
        logger.info("Trigger {}", url)
    }
}
