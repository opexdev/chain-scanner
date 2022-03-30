package co.nilin.opex.chainscan.tron

import co.nilin.opex.chainscan.core.controller.ChainController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(ChainController::class)
class BitcoinScanApplication

fun main(args: Array<String>) {
	runApplication<BitcoinScanApplication>(*args)
}
