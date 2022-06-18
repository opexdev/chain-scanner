package co.nilin.opex.chainscanner.bitcoin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("co.nilin.opex.chainscanner")
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
