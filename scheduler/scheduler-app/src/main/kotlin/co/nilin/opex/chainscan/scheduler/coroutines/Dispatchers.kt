package co.nilin.opex.chainscan.scheduler.coroutines

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object Dispatchers {
    val SCHEDULER = Executors.newFixedThreadPool(64).asCoroutineDispatcher()
}