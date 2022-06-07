package co.nilin.opex.chainscanner.core.utils

import co.nilin.opex.chainscanner.core.exceptions.RateLimitException

object ExceptionHandling {
    fun handleRateLimit(e: Throwable) {
        when (e) {
            is RateLimitException -> throw RateLimitException(e.message)
        }
    }
}
