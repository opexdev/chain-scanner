package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.api.ChainScannerHandler
import co.nilin.opex.chainscan.scheduler.api.ScannerProxy
import co.nilin.opex.chainscan.scheduler.po.TransferResult
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigInteger
import java.util.*

private inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> =
    object : ParameterizedTypeReference<T>() {}

@Service
class ScannerProxyImpl(private val webClient: WebClient, private val chainScannerHandler: ChainScannerHandler) :
    ScannerProxy {
    override suspend fun getTransfers(url: String, blockNumber: BigInteger?): TransferResult {
        return webClient.post()
            .uri {
                it.path(url).queryParamIfPresent("blockNumber", Optional.ofNullable(blockNumber)).build()
            }
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(typeRef<TransferResult>())
            .awaitFirst()
    }
}