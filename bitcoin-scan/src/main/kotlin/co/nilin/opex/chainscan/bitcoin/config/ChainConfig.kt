package co.nilin.opex.chainscan.bitcoin.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient

@Configuration
class ChainConfig {

    @Value("\${app.blockchain.rpc-url}")
    private lateinit var url: String

    @Bean
    fun client(): BitcoinJSONRPCClient {
        return BitcoinJSONRPCClient(url)
    }

}