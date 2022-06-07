package co.nilin.opex.chainscanner.ethereum.utils

import co.nilin.opex.chainscanner.core.exceptions.RateLimitException
import org.web3j.protocol.exceptions.ClientConnectionException

object ExceptionHandling {
    fun detectRateLimit(e: Throwable): Nothing = when (e) {
        is ClientConnectionException -> throw RateLimitException(e.message)
        else -> throw e
    }
}
