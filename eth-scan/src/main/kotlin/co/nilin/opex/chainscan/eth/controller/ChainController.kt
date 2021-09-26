package co.nilin.opex.chainscan.eth.controller

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.eth.impl.ChainService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChainController(private val chainService: ChainService) {
    @GetMapping("/transfers")
    suspend fun getTransfers(startBlock: Long, endBlock: Long, addresses: List<String>): List<Transfer> {
        return chainService.getTransfers(startBlock, endBlock, addresses)
    }
}
