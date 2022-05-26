package co.nilin.opex.chainscan.scannerdb.impl

import co.nilin.opex.chainscan.core.model.ChainSyncRecord
import co.nilin.opex.chainscan.core.model.DepositResult
import co.nilin.opex.chainscan.core.model.Endpoint
import co.nilin.opex.chainscan.core.model.TransfersRequest
import co.nilin.opex.chainscan.core.spi.ChainEndpointProxy
import co.nilin.opex.chainscan.core.spi.FetchAndConvert
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

class ChainEndpointProxyImpl(
    private val fetchAndConvert: FetchAndConvert,
    private val endpoints: List<Endpoint>
) : ChainEndpointProxy {
    data class Transfer(
        var txHash: String,
        var blockNumber: BigInteger,
        var from: String?,
        var to: String?,
        var isTokenTransfer: Boolean,
        var token: String? = null,
        var amount: BigDecimal
    )

    private val logger = LoggerFactory.getLogger(ChainEndpointProxyImpl::class.java)

    private suspend fun requestTransferList(endpoint: String, request: TransfersRequest): DepositResult {
        return fetchAndConvert.fetchAndConvert(endpoint, request)
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
