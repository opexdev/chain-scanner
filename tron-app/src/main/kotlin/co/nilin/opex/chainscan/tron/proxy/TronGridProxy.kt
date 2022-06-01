package co.nilin.opex.chainscan.tron.proxy

import co.nilin.opex.chainscan.core.utils.LoggerDelegate
import co.nilin.opex.chainscan.tron.data.BlockResponse
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import reactor.core.publisher.Mono

@Component
class TronGridProxy(
    private val webClient: WebClient,
    @Value("\${app.rest-api.endpoint}")
    private val url: String,
    @Value("\${app.rest-api.api-key}")
    private val apiKey: String
) {
    private val logger: Logger by LoggerDelegate()

    data class GetBlockRequest(val num: Long)

    suspend fun getBlockByNumber(number: Long): BlockResponse? {
        logger.info("Fetching block data $number")
        return webClient.post()
            .uri("$url/wallet/getblockbynum")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("TRON-PRO-API-KEY", apiKey)
            .body(Mono.just(GetBlockRequest(number)))
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(BlockResponse::class.java)
            .awaitSingleOrNull()
    }

    suspend fun getLatestBlock(): BlockResponse? {
        logger.info("Fetching latest block")
        return webClient.post()
            .uri("$url/wallet/getnowblock")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("TRON-PRO-API-KEY", apiKey)
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(BlockResponse::class.java)
            .awaitSingleOrNull()
    }
}
