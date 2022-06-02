package co.nilin.opex.chainscanner.scheduler.coroutines

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object Dispatchers {
    val SCHEDULER = Executors.newFixedThreadPool(64).asCoroutineDispatcher()
}
