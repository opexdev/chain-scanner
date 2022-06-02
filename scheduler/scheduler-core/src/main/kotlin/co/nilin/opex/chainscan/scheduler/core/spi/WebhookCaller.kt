package co.nilin.opex.chainscan.scheduler.core.spi

import co.nilin.opex.chainscan.scheduler.core.po.Transfer

interface WebhookCaller {
    suspend fun callWebhook(chainName: String, data: List<Transfer>)
}