package co.nilin.opex.chainscan.scheduler.api

import co.nilin.opex.chainscan.scheduler.po.Transfer

interface WebhookCaller {
    suspend fun callWebhook(url: String, data: List<Transfer>)
}
