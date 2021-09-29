package co.nilin.opex.chainscan.bitcoin

import co.nilin.opex.chainscan.bitcoin.data.BlockHashResponse
import co.nilin.opex.chainscan.bitcoin.data.BlockResponse
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GetBlockProxy(private val webClient: WebClient) {

    private val logger = LoggerFactory.getLogger(GetBlockProxy::class.java)

    @Value("\${app.blockchain.rest-url}")
    private lateinit var url: String

    @Value("\${app.blockchain.api-key}")
    private lateinit var apiKey: String

    suspend fun getBlockHash(height: Long): String {
        logger.info("fetching block hash of $height")
        return webClient.get()
            .uri("$url/blockhashbyheight/${height}.json")
            .accept(MediaType.APPLICATION_JSON)
            .header("x-api-key", apiKey)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(BlockHashResponse::class.java)
            .awaitSingleOrNull()
            ?.blockHash ?: ""
    }

    suspend fun getBlockData(hash: String): BlockResponse? {
        logger.info("fetching block data of $hash")
        return webClient.get()
            .uri("$url/block/${hash}.json")
            .accept(MediaType.APPLICATION_JSON)
            .header("x-api-key", apiKey)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(BlockResponse::class.java)
            .awaitSingleOrNull()
    }

}