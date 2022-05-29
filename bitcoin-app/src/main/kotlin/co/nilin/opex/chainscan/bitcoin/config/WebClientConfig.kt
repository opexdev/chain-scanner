package co.nilin.opex.chainscan.bitcoin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun webClient(): WebClient {
        val strategy = ExchangeStrategies.builder()
            .codecs { it.defaultCodecs().maxInMemorySize(20 * 1024 * 1024) }
            .build()
        return WebClient.builder().exchangeStrategies(strategy).build()
    }
}
