package co.nilin.opex.chainscan.scheduler.impl

import co.nilin.opex.chainscan.scheduler.model.TokenAddressModel
import co.nilin.opex.chainscan.scheduler.repository.TokenAddressRepository
import co.nilin.opex.chainscan.scheduler.spi.TokenAddressHandler
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TokenAddressHandlerImpl(private val tokenAddressRepository: TokenAddressRepository) : TokenAddressHandler {
    private val logger = LoggerFactory.getLogger(TokenAddressHandler::class.java)

    override suspend fun addTokenAddress(symbol: String, chainName: String, address: String, memo: String?) {
        try {
            tokenAddressRepository.insert(symbol, chainName, address, memo).awaitSingleOrNull()
        } catch (e: Exception) {
            logger.error("Could not insert new currency $symbol", e)
        }
    }

    override suspend fun deleteTokenAddress(symbol: String) {
        try {
            tokenAddressRepository.deleteByName(symbol).awaitFirstOrNull()
        } catch (e: Exception) {
            logger.error("Could not delete currency $symbol", e)
        }
    }

    override suspend fun findTokenAddresses(chainName: String): List<TokenAddressModel> {
        return tokenAddressRepository.findBySymbol(chainName).collectList().awaitSingle()
    }
}
