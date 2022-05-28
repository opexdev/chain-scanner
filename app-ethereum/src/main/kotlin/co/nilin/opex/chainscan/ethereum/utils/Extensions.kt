package co.nilin.opex.chainscan.ethereum.utils

import org.web3j.crypto.Keys

fun <T> tryOrElse(alt: T, action: () -> T): T {
    return try {
        action()
    } catch (e: Exception) {
        alt
    }
}

fun String.checksumAddress(): String {
    return Keys.toChecksumAddress(this)
}
