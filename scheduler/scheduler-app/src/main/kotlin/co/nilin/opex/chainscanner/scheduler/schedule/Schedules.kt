package co.nilin.opex.chainscanner.scheduler.schedule

import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.schedule.tasks.RetryFailedSyncs
import co.nilin.opex.chainscanner.scheduler.schedule.tasks.SyncLatestTransfers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class Schedules(
    private val chainSyncSchedulerHandler: ChainSyncSchedulerHandler
) {
    @Autowired
    fun `Sync latest transfers`(mainSync: SyncLatestTransfers): ChainSyncScheduleRunner =
        object : ChainSyncScheduleRunner(mainSync, CoroutineScope(Dispatchers.IO), chainSyncSchedulerHandler) {}

    @Autowired
    fun `Retry failed syncs`(retrySync: RetryFailedSyncs): ChainSyncScheduleRunner =
        object : ChainSyncScheduleRunner(retrySync, CoroutineScope(Dispatchers.IO), chainSyncSchedulerHandler) {}
}
