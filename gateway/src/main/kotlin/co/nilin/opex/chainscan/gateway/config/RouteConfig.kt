package co.nilin.opex.chainscan.gateway.config

import co.nilin.opex.chainscan.gateway.repository.ScannerModuleRepository
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RouteConfig(private val scannerModuleRepository: ScannerModuleRepository) {
    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator = builder.routes().apply {
        scannerModuleRepository.findAll().map { scanner ->
            route(scanner.name) {
                it.path("/${scanner.name}/**")
                    .filters { f ->
                        f.rewritePath("/${scanner.name}/(?<segment>.*)", "/\${segment}")
                    }
                    .uri(scanner.url)
            }
        }.subscribe()
    }.build()
}
