package kr.jadekim.standingwater.server.api.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.LoggerContextListener
import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.core.spi.LifeCycle

class LogbackStartupListener : ContextAwareBase(), LoggerContextListener, LifeCycle {

    private var isStarted = false

    override fun start() {
        if (isStarted) {
            return
        }

        val env = System.getenv("SERVICE_ENV")?.toLowerCase()

        when (env) {
            "dev", "qa" -> {
                context.apply {
                    putProperty("db.level", "DEBUG")
                    putProperty("dbcp.level", "INFO")
                    putProperty("redis.level", "INFO")
                    putProperty("koin.level", "WARN")
                    putProperty("server.level", "WARN")
                    putProperty("etc.level", "WARN")
                    putProperty("application.level", "DEBUG")
                }
            }
            "stage", "production" -> {
                context.apply {
                    putProperty("db.level", "WARN")
                    putProperty("dbcp.level", "WARN")
                    putProperty("redis.level", "WARN")
                    putProperty("koin.level", "WARN")
                    putProperty("server.level", "WARN")
                    putProperty("etc.level", "WARN")
                    putProperty("application.level", "INFO")
                }
            }
            else -> {
                context.apply {
                    putProperty("db.level", "DEBUG")
                    putProperty("dbcp.level", "INFO")
                    putProperty("redis.level", "INFO")
                    putProperty("koin.level", "WARN")
                    putProperty("server.level", "INFO")
                    putProperty("etc.level", "INFO")
                    putProperty("application.level", "TRACE")
                }
            }
        }

        isStarted = true
    }

    override fun isStarted(): Boolean = isStarted

    override fun isResetResistant(): Boolean = true

    override fun stop() {
        //do nothing
    }

    override fun onLevelChange(logger: Logger?, level: Level?) {
        //do nothing
    }

    override fun onReset(context: LoggerContext?) {
        //do nothing
    }

    override fun onStart(context: LoggerContext?) {
        //do nothing
    }

    override fun onStop(context: LoggerContext?) {
        //do nothing
    }
}