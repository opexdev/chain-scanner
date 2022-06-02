package co.nilin.opex.chainscanner.bitcoin.proxy

import co.nilin.opex.chainscanner.bitcoin.data.BlockHashResponse
import co.nilin.opex.chainscanner.bitcoin.data.BlockResponse
import co.nilin.opex.chainscanner.bitcoin.data.ChainInfoResponse
import co.nilin.opex.chainscanner.core.utils.LoggerDelegate
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GetBlockProxy(
    private val webClient: WebClient,
    @Value("\${app.rest-api.endpoint}")
    private val endpoint: String,
    @Value("\${app.rest-api.api-key}")
    private val apiKey: String
) {
    private val logger: Logger by LoggerDelegate()

    suspend fun getInfo(): ChainInfoResponse? {
        logger.trace("Fetching chain info")
        return webClient.get()
            .uri("$endpoint/chaininfo.json")
            .accept(MediaType.APPLICATION_JSON)
            .header("x-api-key", apiKey)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(ChainInfoResponse::class.java)
            .awaitSingleOrNull()
    }

    suspend fun getBlockHash(height: Long): String {
        logger.trace("Fetching block hash of $height")
        return webClient.get()
            .uri("$endpoint/blockhashbyheight/${height}.json")
            .accept(MediaType.APPLICATION_JSON)
            .header("x-api-key", apiKey)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(BlockHashResponse::class.java)
            .awaitSingleOrNull()?.blockHash ?: ""
    }

    suspend fun getBlockData(hash: String): BlockResponse? {
        logger.trace("Fetching block data of $hash")
        return webClient.get()
            .uri("$endpoint/block/${hash}.json")
            .accept(MediaType.APPLICATION_JSON)
            .header("x-api-key", apiKey)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(BlockResponse::class.java)
            .awaitSingleOrNull()
    }
}
