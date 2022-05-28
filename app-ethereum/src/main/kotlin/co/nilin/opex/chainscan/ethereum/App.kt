package co.nilin.opex.chainscan.ethereum

import co.nilin.opex.chainscan.core.controller.ScannerController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(ScannerController::class)
class EthScanApplication

fun main(args: Array<String>) {
    runApplication<EthScanApplication>(*args)
}
