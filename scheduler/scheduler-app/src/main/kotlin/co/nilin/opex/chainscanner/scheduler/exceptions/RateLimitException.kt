package co.nilin.opex.chainscanner.scheduler.exceptions

class RateLimitException(val delay: Long, message: String? = null) : Exception(message)
