package co.nilin.opex.chainscanner.scheduler.service

import co.nilin.opex.chainscanner.scheduler.core.po.Transfer
import co.nilin.opex.chainscanner.scheduler.core.spi.ScannerProxy
import co.nilin.opex.chainscanner.scheduler.exceptions.RateLimitException
import co.nilin.opex.chainscanner.scheduler.exceptions.ScannerConnectException
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigInteger
import java.net.ConnectException
import java.net.URI
import java.util.*

private inline fun <reified T : Any> parameterizedTypeReference(): ParameterizedTypeReference<T> =
    object : ParameterizedTypeReference<T>() {}

@Service
class ScannerProxyImpl(private val webClient: WebClient) : ScannerProxy {
    override suspend fun getTransfers(url: String, blockNumber: BigInteger?): List<Transfer> {
        val uri = URI.create("$url/transfers").normalize()
        return runCatching {
            webClient.get()
                .uri {
                    val query = it.queryParamIfPresent("blockNumber", Optional.ofNullable(blockNumber)).build()
                    uri.resolve("$uri$query").normalize()
                }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus({ t -> t.isError }, { it.createException() })
                .bodyToMono(parameterizedTypeReference<List<Transfer>>())
                .awaitFirst()
        }.onFailure {
            if (it is WebClientRequestException && it.isConnectionError)
                throw ScannerConnectException(uri.toString())
            else if (it is WebClientResponseException && it.isTooManyRequests)
                throw RateLimitException("Rate limit on uri: $uri")
        }.getOrThrow()
    }

    override suspend fun getBlockNumber(url: String): BigInteger {
        val uri = URI.create("$url/block-number").normalize()
        return runCatching {
            webClient.get()
                .uri { uri }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus({ t -> t.isError }, { it.createException() })
                .bodyToMono(BigInteger::class.java)
                .awaitFirst()
        }.onFailure {
            if (it is WebClientRequestException && it.isConnectionError)
                throw ScannerConnectException(uri.toString())
            else if (it is WebClientResponseException && it.isTooManyRequests)
                throw RateLimitException("Rate limit on uri: $uri")
        }.getOrThrow()
    }

    override suspend fun clearCache(url: String, blockNumber: BigInteger) {
        val uri = URI.create("$url/clear-cache").normalize()
        runCatching {
            webClient.delete()
                .uri {
                    val query = it.queryParam("blockNumber", blockNumber).build()
                    uri.resolve("$uri$query").normalize()
                }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus({ t -> t.isError }, { it.createException() })
                .bodyToMono(parameterizedTypeReference<List<Transfer>>())
                .awaitFirstOrNull()
        }.onFailure {
            if (it is WebClientRequestException && it.isConnectionError)
                throw ScannerConnectException(uri.toString())
        }.getOrThrow()
    }

    private val WebClientResponseException.isTooManyRequests: Boolean
        get() = statusCode == HttpStatus.TOO_MANY_REQUESTS

    private val WebClientRequestException.isConnectionError: Boolean
        get() = mostSpecificCause is ConnectException
}
