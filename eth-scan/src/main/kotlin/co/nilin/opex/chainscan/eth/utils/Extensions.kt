package co.nilin.opex.chainscan.eth.utils

fun <T> tryOrElse(alt: T, action: () -> T): T {
    return try {
        action()
    } catch (e: Exception) {
        alt
    }
}