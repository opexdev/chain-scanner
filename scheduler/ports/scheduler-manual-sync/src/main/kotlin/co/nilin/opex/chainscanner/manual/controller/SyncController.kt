package co.nilin.opex.chainscanner.manual.controller

import co.nilin.opex.chainscanner.scheduler.core.spi.ChainScannerHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ScannerProxy
import co.nilin.opex.chainscanner.scheduler.core.spi.WebhookCaller
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ResponseStatusException

@RestController
class SyncController(
    private val scannerProxy: ScannerProxy,
    private val chainScannerHandler: ChainScannerHandler,
    private val webhookCaller: WebhookCaller
) {
    @PutMapping("sync/{chain}")
    suspend fun syncTransfers(@PathVariable chain: String, txHash: String) {
        val chainScanner = chainScannerHandler.getScannersByName(chain).firstOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Chain Not Found")
        runCatching {
            scannerProxy.getByTxHash(chainScanner.url, txHash)
        }.onFailure { e ->
            if (e is WebClientResponseException && e.statusCode.is4xxClientError)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Tx Hash")
            else throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }.mapCatching {
            webhookCaller.callWebhook(chain, it)
        }.onFailure { e ->
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }.getOrThrow()
    }
}
