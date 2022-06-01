package co.nilin.opex.chainscan.core.controller

import co.nilin.opex.chainscan.core.exceptions.RateLimitException
import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.service.ChainSyncService
import co.nilin.opex.chainscan.core.spi.BlockchainGateway
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.math.BigInteger

@RestController
class ScannerController(
    private val chainSyncService: ChainSyncService<*>,
    private val blockchainGateway: BlockchainGateway<*>
) {
    @GetMapping("/transfers")
    suspend fun getTransfers(blockNumber: BigInteger?): List<Transfer> {
        return runCatching { chainSyncService.getTransfers(blockNumber) }.onFailure { e ->
            when (e) {
                is RateLimitException -> throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS)
            }
        }.getOrThrow()
    }

    @GetMapping("/block-number")
    suspend fun getBlockNumber(): BigInteger {
        return blockchainGateway.getLatestBlock()
    }
}
