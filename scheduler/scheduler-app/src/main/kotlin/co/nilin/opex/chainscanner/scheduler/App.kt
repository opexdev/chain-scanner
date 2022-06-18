package co.nilin.opex.chainscanner.scheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ComponentScan("co.nilin.opex.chainscanner")
class App

fun main() {
    runApplication<App>()
}
