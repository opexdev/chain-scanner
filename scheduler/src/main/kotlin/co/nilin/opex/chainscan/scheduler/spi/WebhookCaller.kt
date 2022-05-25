package co.nilin.opex.chainscan.scheduler.spi

interface WebhookCaller {
    suspend fun callWebhook(url: String, data: Any)
}
