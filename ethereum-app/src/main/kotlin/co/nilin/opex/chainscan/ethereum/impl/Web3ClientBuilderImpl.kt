package co.nilin.opex.chainscan.ethereum.impl

import co.nilin.opex.chainscan.core.spi.ChainEndpointHandler
import co.nilin.opex.chainscan.ethereum.api.Web3ClientBuilder
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Service
class Web3ClientBuilderImpl(private val chainEndpointHandler: ChainEndpointHandler) : Web3ClientBuilder {
    override suspend fun getWeb3Client(): Web3j {
        val endpoints = chainEndpointHandler.findAll()
        return Web3j.build(HttpService(endpoints.first().url))
    }
}
