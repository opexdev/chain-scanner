package co.nilin.opex.chainscan.tron.utils

suspend fun justTry(body: suspend () -> Unit) {
    try {
        body()
    } catch (_: Exception) {

    }
}

fun <T> tryOrElse(alt: T, action: () -> T): T {
    return try {
        action()
    } catch (e: Exception) {
        alt
    }
}

inline fun <T> tryOrNull(action: () -> T): T? {
    return try {
        action()
    } catch (e: Exception) {
        null
    }
}

fun String.asBase58Check(): String = Base58Check.toBase58Address(this)

fun String?.asTronAddress(): String? = if (this == null)
    null
else if (startsWith("00"))
    "41${substring(2)}".asBase58Check()
else
    asBase58Check()