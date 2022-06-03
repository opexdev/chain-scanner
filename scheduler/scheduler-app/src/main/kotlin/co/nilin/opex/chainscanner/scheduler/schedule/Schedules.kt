package co.nilin.opex.chainscanner.scheduler.schedule

import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.schedule.tasks.RetryFailedSyncs
import co.nilin.opex.chainscanner.scheduler.schedule.tasks.SyncLatestTransfers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.stereotype.Service

@Service
class `Sync latest transfers`(chainSyncSchedulerHandler: ChainSyncSchedulerHandler, mainSync: SyncLatestTransfers) :
    ChainSyncScheduleRunner(mainSync, CoroutineScope(Dispatchers.IO), chainSyncSchedulerHandler)

@Service
class `Retry failed syncs`(chainSyncSchedulerHandler: ChainSyncSchedulerHandler, retrySync: RetryFailedSyncs) :
    ChainSyncScheduleRunner(retrySync, CoroutineScope(Dispatchers.IO), chainSyncSchedulerHandler)
