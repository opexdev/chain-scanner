package co.nilin.opex.chainscan.scheduler.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LoggerDelegate : ReadOnlyProperty<Any?, Logger> {
    private lateinit var logger: Logger

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger {
        if (!::logger.isInitialized) logger = LoggerFactory.getLogger(thisRef!!.javaClass)
        return logger
    }
}