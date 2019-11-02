package kr.jadekim.standingwater.server.api

import kr.jadekim.enumuration.ServiceEnvironment
import kr.jadekim.logger.KoinLogger
import kr.jadekim.standingwater.server.api.presenter.NFAuthApiServer
import kr.jadekim.standingwater.server.api.module.DBModule
import kr.jadekim.standingwater.server.api.module.RedisModule
import kr.jadekim.standingwater.server.api.module.RepositoryModule
import kr.jadekim.standingwater.server.api.module.ServiceModule
import kr.jadekim.util.loadProperties
import kr.jadekim.util.shutdownHook
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.koin.experimental.builder.single

fun main(vararg args: String) {
    println("Startup NFAUTH-API(${System.currentTimeMillis()})")

    val serviceEnvValue = System.getenv("SERVICE_ENV")?.toLowerCase() ?: "local"
    val serviceEnv = ServiceEnvironment.of(serviceEnvValue)
        ?: throw IllegalArgumentException("Invalid SERVICE_ENV value")

    val envProperties = NFAuthApiServer::class.java.classLoader
        .getResourceAsStream("$serviceEnvValue.properties")
        ?: throw IllegalArgumentException("Can't load $serviceEnvValue.properties")

    val properties = loadProperties(envProperties)

    val koin = startKoin {
        logger(KoinLogger())
        koin.propertyRegistry.saveProperties(properties)
        modules(module {
            single { properties }
            single { serviceEnv }
            single<NFAuthApiServer>().onClose { it?.stop() }
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

    shutdownHook { koin.close() }

    koin.get<NFAuthApiServer>().start()
}