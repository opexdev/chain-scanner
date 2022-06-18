package co.nilin.opex.chainscanner.tron.utils

fun String.asBase58Check(): String = Base58Check.toBase58Address(this)

fun String.asTronAddress() = if (startsWith("00")) "41${substring(2)}".asBase58Check() else asBase58Check()
