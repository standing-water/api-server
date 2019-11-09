package kr.jadekim.standingwater.server.api

import kotlinx.coroutines.channels.Channel
import kr.jadekim.enumuration.ServiceEnvironment
import kr.jadekim.logger.KoinLogger
import kr.jadekim.standingwater.domain.Event
import kr.jadekim.standingwater.server.api.module.DBModule
import kr.jadekim.standingwater.server.api.module.RedisModule
import kr.jadekim.standingwater.server.api.module.RepositoryModule
import kr.jadekim.standingwater.server.api.module.ServiceModule
import kr.jadekim.standingwater.server.api.presenter.StandingWaterApiServer
import kr.jadekim.standingwater.service.RealtimeService
import kr.jadekim.util.loadProperties
import kr.jadekim.util.shutdownHook
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.koin.experimental.builder.single
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("STANDING-WATER-API")

fun main(vararg args: String) {
    logger.info("Startup STANDING-WATER-API")

    val serviceEnvValue = System.getenv("SERVICE_ENV")?.toLowerCase() ?: "local"
    val serviceEnv = ServiceEnvironment.of(serviceEnvValue)
        ?: throw IllegalArgumentException("Invalid SERVICE_ENV value")

    val envProperties = StandingWaterApiServer::class.java.classLoader
        .getResourceAsStream("$serviceEnvValue.properties")
        ?: throw IllegalArgumentException("Can't load $serviceEnvValue.properties")

    val properties = loadProperties(envProperties)

    val koin = startKoin {
        logger(KoinLogger())
        koin.propertyRegistry.saveProperties(properties)
        modules(module {
            single { properties }
            single { serviceEnv }
            single<StandingWaterApiServer>().onClose { it?.stop() }
        })
        modules(
            listOf(
                DBModule,
                RedisModule,
                RepositoryModule,
                ServiceModule
            )
        )
    }.koin

    shutdownHook {
        koin.close()
        koin.get<Channel<Pair<Int, Event>>>().close()
    }

    koin.get<RealtimeService>().startup()
    koin.get<StandingWaterApiServer>().start()
}