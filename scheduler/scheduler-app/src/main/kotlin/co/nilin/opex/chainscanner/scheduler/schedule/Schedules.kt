package co.nilin.opex.chainscanner.scheduler.schedule

import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.schedule.tasks.RetryFailedSyncs
import co.nilin.opex.chainscanner.scheduler.schedule.tasks.SyncLatestTransfers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.stereotype.Service

@Service
class SyncLatestTransfersSchedule(chainSyncSchedulerHandler: ChainSyncSchedulerHandler, sync: SyncLatestTransfers) :
    ChainSyncScheduleRunner(sync, CoroutineScope(Dispatchers.IO), chainSyncSchedulerHandler)

@Service
class RetryFailedSyncsSchedule(chainSyncSchedulerHandler: ChainSyncSchedulerHandler, retry: RetryFailedSyncs) :
    ChainSyncScheduleRunner(retry, CoroutineScope(Dispatchers.IO), chainSyncSchedulerHandler)
