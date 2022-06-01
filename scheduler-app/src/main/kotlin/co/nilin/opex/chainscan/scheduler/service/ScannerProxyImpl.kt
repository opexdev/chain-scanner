package co.nilin.opex.chainscan.scheduler.service

import co.nilin.opex.chainscan.scheduler.api.ScannerProxy
import co.nilin.opex.chainscan.scheduler.po.TransferResult
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigInteger
import java.net.URI
import java.util.*

private inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> =
    object : ParameterizedTypeReference<T>() {}

@Service
class ScannerProxyImpl(private val webClient: WebClient) : ScannerProxy {
    override suspend fun getTransfers(url: String, blockNumber: BigInteger?): TransferResult {
        return webClient.get()
            .uri {
                it.path("$url/transfers").queryParamIfPresent("blockNumber", Optional.ofNullable(blockNumber)).build()
            }
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(typeRef<TransferResult>())
            .awaitFirst()
    }

    override suspend fun getBlockNumber(url: String): BigInteger {
        return webClient.get()
            .uri { URI.create("$url/block-number") }
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(typeRef<BigInteger>())
            .awaitFirst()
    }
}