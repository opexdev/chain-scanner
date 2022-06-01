package co.nilin.opex.chainscan.scheduler.config

import co.nilin.opex.chainscan.scheduler.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import org.springframework.context.annotation.Bean

class ScopeRepository {
    @Bean("mainSyncScope")
    private fun mainSyncScope() = CoroutineScope(Dispatchers.SCHEDULER)

    @Bean("retrySyncScope")
    private fun retrySyncScope() = CoroutineScope(Dispatchers.SCHEDULER)
}