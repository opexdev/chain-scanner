package co.nilin.opex.chainscan.tron.coroutines

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object Dispatchers {
    val SYNC = Executors.newFixedThreadPool(64).asCoroutineDispatcher()
}