package co.nilin.opex.chainscan.scheduler.sample

import co.nilin.opex.chainscan.scheduler.po.ChainSyncSchedule
import java.time.LocalDateTime
import java.time.ZoneOffset

object VALID {
    const val TIMESTAMP = 1653659069L

    val SCHEDULE =
        ChainSyncSchedule("bitcoin", LocalDateTime.ofEpochSecond(TIMESTAMP + 15, 0, ZoneOffset.UTC), 1500, 1500)
}
