package co.nilin.opex.chainscan.core.controller

import co.nilin.opex.chainscan.core.model.Transfer
import co.nilin.opex.chainscan.core.model.TransfersRequest
import co.nilin.opex.chainscan.core.spi.Chain
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ChainController(private val chain: Chain) {

    @GetMapping("/transfers")
    suspend fun getTransfers(@RequestBody request: TransfersRequest): List<Transfer> {
        return chain.getTransfers(request.startBlock, request.endBlock, request.addresses)
    }
}