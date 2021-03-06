package co.nilin.opex.chainscanner.tron.proxy

import co.nilin.opex.chainscanner.core.utils.LoggerDelegate
import co.nilin.opex.chainscanner.tron.data.BlockResponse
import co.nilin.opex.chainscanner.tron.data.TransactionResponse
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
    data class GetTransactionRequest(val value: String)

    suspend fun getBlockByNumber(number: Long): BlockResponse? {
        logger.debug("Fetching block data $number")
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
        logger.debug("Fetching latest block")
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

    suspend fun getTransactionByHash(hash: String): TransactionResponse? {
        logger.debug("Fetching tx data $hash")
        return webClient.post()
            .uri("$url/wallet/gettransactionbyid")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("TRON-PRO-API-KEY", apiKey)
            .body(Mono.just(GetTransactionRequest(hash)))
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(TransactionResponse::class.java)
            .awaitSingleOrNull()
    }
}
