package co.nilin.opex.chainscan.tron.utils

fun <T> tryOrElse(alt: T, action: () -> T): T {
    return try {
        action()
    } catch (e: Exception) {
        alt
    }
}

fun String.asBase58Check(): String = Base58Check.toBase58Address(this)

fun String.asTronAddress(): String = if (startsWith("00")) "41${substring(2)}".asBase58Check()
else asBase58Check()