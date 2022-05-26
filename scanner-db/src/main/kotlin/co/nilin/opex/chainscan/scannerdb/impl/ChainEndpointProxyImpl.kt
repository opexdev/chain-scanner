package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.model.Deposit
import co.nilin.opex.chainscan.core.model.DepositResult
import co.nilin.opex.chainscan.core.model.Endpoint
import co.nilin.opex.chainscan.core.spi.ChainEndpointProxy
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.body
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.time.LocalDateTime

inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

class ChainEndpointProxyImpl(
    private val chain: String,
    private val endpoints: List<Endpoint>,
    private val webClient: WebClient
) : ChainEndpointProxy {
    data class TransfersRequest(
        val startBlock: BigInteger?,
        val endBlock: BigInteger?,
        val addresses: List<String>?
    )

    data class Transfer(
        var txHash: String,
        var blockNumber: BigInteger,
        var from: String?,
        var to: String?,
        var isTokenTransfer: Boolean,
        var token: String? = null,
        var amount: BigDecimal
    )

    data class TransferResponse(
        val latestBlock: BigInteger,
        val transfers: List<Transfer>
    )

    private val logger = LoggerFactory.getLogger(ChainEndpointProxyImpl::class.java)

    private suspend fun requestTransferList(endpoint: String, request: TransfersRequest): DepositResult {
        val response = webClient.post()
            .uri(URI.create(endpoint))
            .header("Content-Type", "application/json")
            .body(Mono.just(request))
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .bodyToMono(typeRef<TransferResponse>())
            .awaitFirstOrNull()
        val blockNumber = response?.latestBlock ?: request.startBlock ?: BigInteger.ZERO
        return DepositResult(
            ChainSyncRecord(null, LocalDateTime.now(), endpoint, blockNumber),
            response?.transfers
                ?.map {
                    Deposit(
                        null,
                        it.txHash,
                        it.blockNumber,
                        it.to ?: "",
                        null,
                        it.amount,
                        chain,
                        it.isTokenTransfer,
                        it.token
                    )
                }
                ?: emptyList()
        )
    }

    private suspend fun roundRobin(i: Int, request: TransfersRequest): DepositResult {
        return try {
            val response =
                requestTransferList(
                    endpoints[i].url,
                    request
                )
            logger.info("fetched transactions: ${response.deposits.size} transaction received")
            DepositResult(
                ChainSyncRecord(
                    null,
                    LocalDateTime.now(),
                    endpoints[i].url,
                    response.chainSyncRecord.blockNumber
                ),
                emptyList()
            )
        } catch (error: WebClientResponseException) {
            if (i < endpoints.size - 1) {
                roundRobin(i + 1, request)
            } else {
                DepositResult(
                    ChainSyncRecord(
                        null,
                        LocalDateTime.now(),
                        endpoints[i].url,
                        request.endBlock ?: BigInteger.ZERO
                    ),
                    emptyList()
                )
            }
        }
    }

    override suspend fun syncTransfers(filter: ChainEndpointProxy.DepositFilter): DepositResult {
        return roundRobin(0, TransfersRequest(filter.startBlock, filter.endBlock, filter.tokenAddresses))
    }
}
