package co.nilin.opex.chainscan.core.controller

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.service.ChainSyncService
import co.nilin.opex.chainscan.core.spi.GetBlockNumber
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
class ScannerController(private val chainSyncService: ChainSyncService<*>, private val getBlockNumber: GetBlockNumber) {
    @GetMapping("/transfers")
    suspend fun getTransfers(blockNumber: BigInteger?): List<Transfer> {
        return chainSyncService.getTransfers(blockNumber)
    }

    @GetMapping("/block-number")
    suspend fun getBlockNumber(): BigInteger {
        return getBlockNumber.invoke()
    }
}
