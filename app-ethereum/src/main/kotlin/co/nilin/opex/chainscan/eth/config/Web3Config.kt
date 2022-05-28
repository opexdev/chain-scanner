package co.nilin.opex.chainscan.eth.config

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import java.util.concurrent.TimeUnit

@Configuration
class Web3Config {

    private val logger = LoggerFactory.getLogger(Web3Config::class.java)

    @Bean
    fun web3Client(@Value("\${blockchain.url}") url: String): Web3j {
//        logger.info("RPC base url: $url")
//        val builder = OkHttpClient.Builder()
//            .readTimeout(60, TimeUnit.SECONDS)
//            .connectTimeout(60, TimeUnit.SECONDS)
//
//        val interceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
//        builder.addInterceptor(interceptor)
//
//        return Web3j.build(
//            HttpService(
//                url,
//                builder.build(),
//                false
//            )
//        )
        return Web3j.build(HttpService(url))
    }

}