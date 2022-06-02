package co.nilin.opex.chainscanner.database.config

import co.nilin.opex.chainscanner.core.model.WatchListItem
import co.nilin.opex.chainscanner.core.spi.WatchListHandler
import co.nilin.opex.chainscanner.preferences.Preferences
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@DependsOn("postgresConfig")
class InitializeService(
    @Value("\${app.chain.name}") private val chainName: String,
    private val watchListHandler: WatchListHandler
) {
    @Autowired
    private lateinit var preferences: Preferences

    @PostConstruct
    fun init() = runBlocking {
        preferences.currencies.forEach { c ->
            c.implementations.filter { it.chain == chainName }.forEach { ci ->
                ci.tokenAddress?.also {
                    runCatching { watchListHandler.add(WatchListItem(it)) }
                }
            }
        }
    }
}
