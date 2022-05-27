package co.nilin.opex.chainscan.scheduler.api

interface WebhookCaller {
    suspend fun callWebhook(url: String, data: Any)
}
