package co.nilin.opex.chainscanner.ethereum.utils

import org.web3j.crypto.Keys

fun String.checksumAddress(): String = Keys.toChecksumAddress(this)
