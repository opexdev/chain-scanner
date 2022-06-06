package co.nilin.opex.chainscanner.scheduler.service

import co.nilin.opex.chainscanner.scheduler.core.po.Transfer
import co.nilin.opex.chainscanner.scheduler.core.spi.ScannerProxy
import co.nilin.opex.chainscanner.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.slf4j.Logger
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigInteger
import java.net.URI
import java.util.*

private inline fun <reified T : Any> parameterizedTypeReference(): ParameterizedTypeReference<T> =
    object : ParameterizedTypeReference<T>() {}

@Service
class ScannerProxyImpl(private val webClient: WebClient) : ScannerProxy {
    private val logger: Logger by LoggerDelegate()

    override suspend fun getTransfers(url: String, blockNumber: BigInteger?): List<Transfer> {
        return webClient.get()
            .uri {
                URI.create(url).resolve(
                    it.path("/transfers")
                        .queryParamIfPresent("blockNumber", Optional.ofNullable(blockNumber))
                        .build()
                )
            }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(parameterizedTypeReference<List<Transfer>>())
            .awaitFirst()
    }

    override suspend fun getBlockNumber(url: String): BigInteger {
        return webClient.get()
            .uri { URI.create("$url/block-number").normalize() }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(BigInteger::class.java)
            .awaitFirst()
    }

    override suspend fun clearCache(url: String, blockNumber: BigInteger) {
        webClient.delete()
            .uri {
                URI.create(url).resolve(
                    it.path("/clear-cache").queryParam("blockNumber", blockNumber).build()
                )
            }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(parameterizedTypeReference<List<Transfer>>())
            .awaitFirstOrNull()
    }
}
