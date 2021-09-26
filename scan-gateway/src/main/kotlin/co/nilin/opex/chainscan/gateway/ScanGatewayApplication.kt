package co.nilin.opex.chainscan.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScanGatewayApplication

fun main(args: Array<String>) {
	runApplication<ScanGatewayApplication>(*args)
}
