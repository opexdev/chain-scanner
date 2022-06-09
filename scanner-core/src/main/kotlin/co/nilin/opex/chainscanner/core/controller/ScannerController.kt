package co.nilin.opex.chainscanner.core.controller

import co.nilin.opex.chainscanner.core.exceptions.RateLimitException
import co.nilin.opex.chainscanner.core.model.Transfer
import co.nilin.opex.chainscanner.core.service.SyncService
import co.nilin.opex.chainscanner.core.spi.ChainService
import co.nilin.opex.chainscanner.core.spi.TransferCacheHandler
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.math.BigInteger

@RestController
class ScannerController(
    private val syncService: SyncService<*>,
    private val chainService: ChainService<*>,
    private val transferCacheHandler: TransferCacheHandler
) {
    @GetMapping("/transfers")
    suspend fun getTransfers(blockNumber: BigInteger?): List<Transfer> = runCatching {
        blockNumberCheck(blockNumber)
        syncService.getTransfers(blockNumber)
    }.onFailure(::handleRateLimit).onFailure(::handleUnexpected).getOrThrow()

    @GetMapping("/block-number")
    suspend fun getBlockNumber(): BigInteger = runCatching {
        chainService.getLatestBlock()
    }.onFailure(::handleRateLimit).onFailure(::handleUnexpected).getOrThrow()

    @DeleteMapping("/clear-cache")
    suspend fun clearCache(blockNumber: BigInteger) = runCatching {
        blockNumberCheck(blockNumber)
        transferCacheHandler.clearCache(blockNumber)
    }.onFailure(::handleUnexpected).getOrThrow()

    private fun handleRateLimit(e: Throwable) {
        if (e is RateLimitException) throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS)
    }

    private fun handleUnexpected(e: Throwable) {
        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
    }

    private fun blockNumberCheck(blockNumber: BigInteger?) {
        val m = "`blockNumber` must be positive"
        if (blockNumber?.abs() !== blockNumber) throw ResponseStatusException(HttpStatus.BAD_REQUEST, m)
    }
}
