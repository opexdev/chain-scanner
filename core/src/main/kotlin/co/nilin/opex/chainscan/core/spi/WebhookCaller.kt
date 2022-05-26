package co.nilin.opex.chainscan.core.spi

interface WebhookCaller {
    suspend fun callWebhook(url: String, data: Any)
}
