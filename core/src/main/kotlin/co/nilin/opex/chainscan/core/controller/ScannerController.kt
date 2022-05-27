package co.nilin.opex.chainscan.core.controller

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.service.ChainSyncService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
class ScannerController(private val chainSyncService: ChainSyncService) {
    private val logger = LoggerFactory.getLogger(ScannerController::class.java)

    @PostMapping("/transfers")
    suspend fun getTransfers(start: BigInteger, end: BigInteger): List<Transfer> {
        return chainSyncService.getTransfers(start, end)
    }
}
