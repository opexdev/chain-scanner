package co.nilin.opex.chainscan.bitcoin.utils

suspend fun justTry(body: suspend () -> Unit) {
    try {
        body()
    } catch (e: Exception) {

    }
}

suspend fun <T> justTryOrNull(body: suspend () -> T): T? {
    return try {
        body()
    } catch (e: Exception) {
        null
    }
}