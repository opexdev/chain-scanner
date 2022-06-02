package co.nilin.opex.chainscanner.scheduler.config

import co.nilin.opex.chainscanner.scheduler.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ScopeRepository {
    @Bean
    fun syncLatestTransfersScope() = CoroutineScope(Dispatchers.SCHEDULER)

    @Bean
    fun retryFailedSyncsScope() = CoroutineScope(Dispatchers.SCHEDULER)
}