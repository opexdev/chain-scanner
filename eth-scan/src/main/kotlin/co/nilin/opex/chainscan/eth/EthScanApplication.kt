package co.nilin.opex.chainscan.eth

import co.nilin.opex.chainscan.core.controller.ChainController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(ChainController::class)
class EthScanApplication

fun main(args: Array<String>) {
	runApplication<EthScanApplication>(*args)
}
