package co.nilin.opex.chainscanner.bitcoin.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Config {
    @Bean
    fun mapper(): ObjectMapper = ObjectMapper().registerKotlinModule()
}
