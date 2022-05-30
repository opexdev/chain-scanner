package co.nilin.opex.chainscan.scheduler.sample

import co.nilin.opex.chainscan.scheduler.po.*
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.ZoneOffset

object VALID {
    private const val TIMESTAMP = 1653659069L

    const val BITCOIN = "bitcoin"

    val CURRENT_LOCAL_DATE_TIME: LocalDateTime = LocalDateTime.ofEpochSecond(TIMESTAMP, 0, ZoneOffset.UTC)

    val SCHEDULE =
        ChainSyncSchedule(BITCOIN, LocalDateTime.ofEpochSecond(TIMESTAMP + 15, 0, ZoneOffset.UTC), 1500, 1500)

    val CHAIN_SYNC_RECORD = ChainSyncRecord(BITCOIN, CURRENT_LOCAL_DATE_TIME, BigInteger.ZERO)

    val CHAIN_SCANNER = ChainScanner(
        BITCOIN,
        "http://bitcoin-scanner",
        30,
        1
    )

    val NEW_CHAIN_SYNC_RETRY = ChainSyncRetry(
        BITCOIN,
        BigInteger.ZERO,
        BigInteger.ZERO,
        retries = 0
    )

    private val TRANSFER = Transfer(
        "TX_HASH",
        BigInteger.ZERO,
        Wallet("ADDRESS"),
        Wallet("ADDRESS"),
        false,
        BigDecimal.valueOf(0.0001),
        BITCOIN
    )

    val TRANSFER_RESULT = TransferResult(BigInteger.ZERO, BigInteger.ZERO, listOf(TRANSFER))
}
