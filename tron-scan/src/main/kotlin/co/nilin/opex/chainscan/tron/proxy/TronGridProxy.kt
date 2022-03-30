package co.nilin.opex.chainscan.tron.proxy

import co.nilin.opex.chainscan.tron.data.BlockResponse
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import reactor.core.publisher.Mono

@Component
class TronGridProxy(private val webClient: WebClient) {

    private val logger = LoggerFactory.getLogger(TronGridProxy::class.java)

    @Value("\${app.blockchain.rest-url}")
    private lateinit var url: String

    @Value("\${app.blockchain.api-key}")
    private lateinit var apiKey: String

    data class GetBlockRequest(val num: Long)

    suspend fun getBlockByNumber(number: Long): BlockResponse? {
        logger.info("fetching block data $number")
        return webClient.post()
            .uri("$url/wallet/getblockbynum")
            .body(Mono.just(GetBlockRequest(number)))
            .accept(MediaType.APPLICATION_JSON)
            .header("TRON-PRO-API-KEY", apiKey)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(BlockResponse::class.java)
            .awaitSingleOrNull()
    }

}