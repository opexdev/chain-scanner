package co.nilin.opex.chainscan.eth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EthScanApplication

fun main(args: Array<String>) {
	runApplication<EthScanApplication>(*args)
}
