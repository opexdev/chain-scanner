package co.nilin.opex.chainscanner.ethereum.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Configuration
class Web3Config(@Value("\${app.rpc.endpoint}") private val endpoint: String) {
    @Bean
    fun web3Client(): Web3j = Web3j.build(HttpService(endpoint))
}
