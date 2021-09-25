package co.nilin.opex.chainscan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("co.nilin.opex")
class ChainScan

fun main(args: Array<String>) {
    runApplication<ChainScan>(*args)
}
