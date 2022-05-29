package co.nilin.opex.chainscan.tron.config

import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun webClient(loadBalancerFactory: ReactiveLoadBalancer.Factory<ServiceInstance>): WebClient {
        val strategy = ExchangeStrategies.builder()
            .codecs { it.defaultCodecs().maxInMemorySize(20 * 1024 * 1024) }
            .build()
        return WebClient.builder().exchangeStrategies(strategy).build()
    }
}
