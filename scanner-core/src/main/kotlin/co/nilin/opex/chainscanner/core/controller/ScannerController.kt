package co.nilin.opex.chainscanner.core.controller

import co.nilin.opex.chainscanner.core.exceptions.RateLimitException
import co.nilin.opex.chainscanner.core.model.Transfer
import co.nilin.opex.chainscanner.core.service.SyncService
import co.nilin.opex.chainscanner.core.spi.ChainService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.math.BigInteger

@RestController
class ScannerController(
    private val syncService: SyncService<*>,
    private val chainService: ChainService<*>
) {
    @GetMapping("/transfers")
    suspend fun getTransfers(blockNumber: BigInteger?): List<Transfer> {
        return runCatching { syncService.getTransfers(blockNumber) }.onFailure { e ->
            when (e) {
                is RateLimitException -> throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS)
            }
        }.getOrThrow()
    }

    @GetMapping("/block-number")
    suspend fun getBlockNumber(): BigInteger {
        return chainService.getLatestBlock()
    }
}
