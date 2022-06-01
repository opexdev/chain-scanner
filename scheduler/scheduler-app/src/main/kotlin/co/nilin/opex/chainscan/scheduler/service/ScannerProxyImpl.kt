package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.core.spi.ScannerProxy
import co.nilin.opex.chainscan.scheduler.core.po.Transfer
import co.nilin.opex.chainscan.scheduler.utils.LoggerDelegate
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigInteger
import java.net.URI
import java.util.*

private inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> =
    object : ParameterizedTypeReference<T>() {}

@Service
class ScannerProxyImpl(private val webClient: WebClient) : ScannerProxy {
    private val logger: Logger by LoggerDelegate()

    override suspend fun getTransfers(url: String, blockNumber: BigInteger?): List<Transfer> {
        return webClient.get()
            .uri {
                URI.create(url).resolve(
                    it.path("/transfers").queryParamIfPresent("blockNumber", Optional.ofNullable(blockNumber))
                        .build()
                )
            }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(typeRef<List<Transfer>>())
            .awaitFirst()
    }

    override suspend fun getBlockNumber(url: String): BigInteger {
        return webClient.get()
            .uri {
                URI.create(url).resolve(it.path("/block-number").build())
            }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(typeRef<BigInteger>())
            .awaitFirst()
    }
}
