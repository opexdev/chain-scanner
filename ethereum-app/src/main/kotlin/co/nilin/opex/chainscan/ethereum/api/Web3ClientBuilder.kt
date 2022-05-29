package co.nilin.opex.chainscan.ethereum.api

import org.web3j.protocol.Web3j

interface Web3ClientBuilder {
    suspend fun getWeb3Client(): Web3j
}