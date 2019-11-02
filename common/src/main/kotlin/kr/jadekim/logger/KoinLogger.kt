package kr.jadekim.logger

import org.koin.core.Koin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.slf4j.LoggerFactory

class KoinLogger(level: Level = Level.INFO) : Logger(level) {

    private val logger = LoggerFactory.getLogger(Koin::class.java)

    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> logger.debug(msg)
            Level.INFO -> logger.info(msg)
            Level.ERROR -> logger.error(msg)
        }
    }
}