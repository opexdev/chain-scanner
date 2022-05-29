package co.nilin.opex.chainscan.ethereum

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("co.nilin.opex.chainscan")
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
