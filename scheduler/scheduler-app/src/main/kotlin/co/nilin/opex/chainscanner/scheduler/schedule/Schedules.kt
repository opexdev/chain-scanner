package co.nilin.opex.chainscanner.scheduler.schedule

import co.nilin.opex.chainscanner.scheduler.core.spi.ChainScannerHandler
import co.nilin.opex.chainscanner.scheduler.core.spi.ChainSyncSchedulerHandler
import co.nilin.opex.chainscanner.scheduler.coroutines.Dispatchers
import co.nilin.opex.chainscanner.scheduler.schedule.tasks.RetryFailedSyncs
import co.nilin.opex.chainscanner.scheduler.schedule.tasks.SyncLatestTransfers
import kotlinx.coroutines.CoroutineScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SyncLatestTransfersSchedule(
    chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    chainScannerHandler: ChainScannerHandler,
    sync: SyncLatestTransfers,
    @Value("\${app.schedule.error-rate}")
    errorRate: Int,
    @Value("\${app.schedule.error-rate-period}")
    errorRatePeriod: Int
) : ChainSyncScheduleRunner(
    sync,
    CoroutineScope(Dispatchers.SCHEDULER),
    chainSyncSchedulerHandler,
    chainScannerHandler,
    errorRate,
    errorRatePeriod
)

@Service
class RetryFailedSyncsSchedule(
    chainSyncSchedulerHandler: ChainSyncSchedulerHandler,
    chainScannerHandler: ChainScannerHandler,
    retry: RetryFailedSyncs,
    @Value("\${app.schedule.error-rate}")
    errorRate: Int,
    @Value("\${app.schedule.error-rate-period}")
    errorRatePeriod: Int
) : ChainSyncScheduleRunner(
    retry,
    CoroutineScope(Dispatchers.SCHEDULER),
    chainSyncSchedulerHandler,
    chainScannerHandler,
    errorRate,
    errorRatePeriod
)
