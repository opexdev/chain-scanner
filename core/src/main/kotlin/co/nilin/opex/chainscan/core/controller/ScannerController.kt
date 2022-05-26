package co.nilin.opex.chainscan.core.controller

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.service.ChainSyncService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
class ScannerController(private val chainSyncService: ChainSyncService) {
    private val logger = LoggerFactory.getLogger(ScannerController::class.java)

    @PostMapping("/transfers/{consumerId}")
    suspend fun getTransfers(@PathVariable consumerId: Long, batch: Int?): List<Transfer> {
        return chainSyncService.getTransfers(batch ?: 30)
    }

    @DeleteMapping("/clear-cache/{consumerId}")
    suspend fun clearCache(@PathVariable consumerId: Long, blockNumber: BigInteger) {
        return chainSyncService.clearCache(consumerId, blockNumber)
    }
}
