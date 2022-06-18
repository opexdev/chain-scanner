package co.nilin.opex.chainscanner.scheduler.core.spi

import co.nilin.opex.chainscanner.scheduler.core.po.Transfer

interface WebhookCaller {
    suspend fun callWebhook(chainName: String, data: List<Transfer>)
}
