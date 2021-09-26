package co.nilin.opex.chainscan.gateway.config

import co.nilin.opex.chainscan.gateway.repository.ScannerRepository
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RouteConfig(private val scannerRepository: ScannerRepository) {

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator = builder.routes().apply {
        scannerRepository.findAll().map { scanner ->
            route(scanner.name) {
                it.path("/${scanner.name}/**")
                    .filters { f ->
                        f.rewritePath("/${scanner.name}/(?<segment>.*)", "/\${segment}")
                    }
                    .uri(scanner.url)
            }
        }
    }.build()

}