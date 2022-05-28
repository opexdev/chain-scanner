package co.nilin.opex.chainscan.scheduler.sample

import co.nilin.opex.chainscan.scheduler.po.ChainSyncRecord
import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.ZoneOffset

object VALID {
    private const val TIMESTAMP = 1653659069L

    private const val BITCOIN = "bitcoin"

    val CURRENT_LOCAL_DATE_TIME: LocalDateTime = LocalDateTime.ofEpochSecond(TIMESTAMP, 0, ZoneOffset.UTC)

    val SCHEDULE =
        ChainSyncSchedule(BITCOIN, LocalDateTime.ofEpochSecond(TIMESTAMP + 15, 0, ZoneOffset.UTC), 1500, 1500)

    val CHAIN_SYNC_RECORD = ChainSyncRecord(BITCOIN, CURRENT_LOCAL_DATE_TIME, BigInteger.ZERO)
}
