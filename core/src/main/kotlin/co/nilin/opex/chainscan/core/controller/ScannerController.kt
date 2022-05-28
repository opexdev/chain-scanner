package co.nilin.opex.chainscan.core.controller

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.service.ChainSyncService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
class ScannerController(private val chainSyncService: ChainSyncService<*>) {
    @PostMapping("/transfers")
    suspend fun getTransfers(start: BigInteger?, end: BigInteger?): List<Transfer> {
        return chainSyncService.getTransfers(start, end)
    }
}
