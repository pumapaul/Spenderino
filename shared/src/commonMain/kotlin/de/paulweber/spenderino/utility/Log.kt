package de.paulweber.spenderino.utility

import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

object Log : Logger() {
    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.INFO -> co.touchlab.kermit.Logger.i(msg)
            Level.NONE -> co.touchlab.kermit.Logger.v(msg)
            Level.ERROR -> co.touchlab.kermit.Logger.e(msg)
            Level.DEBUG -> co.touchlab.kermit.Logger.d(msg)
        }
    }
}
