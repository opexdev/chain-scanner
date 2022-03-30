package co.nilin.opex.chainscan.tron.utils

suspend fun justTry(body: suspend () -> Unit) {
    try {
        body()
    } catch (e: Exception) {

    }
}

fun <T> tryOrElse(alt: T, action: () -> T): T {
    return try {
        action()
    } catch (e: Exception) {
        alt
    }
}