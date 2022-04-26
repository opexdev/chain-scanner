package co.nilin.opex.chainscan.core.controller

import co.nilin.opex.chainscan.core.model.TransfersRequest
import co.nilin.opex.chainscan.core.model.TransfersResult
import co.nilin.opex.chainscan.core.spi.Chain
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ChainController(private val chainService: Chain) {

    private val logger = LoggerFactory.getLogger(ChainController::class.java)

    @Value("\${spring.application.name}")
    private lateinit var appName: String

    @PostMapping("/transfers")
    suspend fun getTransfers(@RequestBody request: TransfersRequest): TransfersResult {
        logger.info("Calling '/transfers' for: $appName")
        return chainService.getTransfers(
            request.startBlock,
            request.endBlock,
            request.addresses?.map { it.lowercase() })
    }
}
