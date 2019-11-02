package kr.jadekim.ktor

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.routing.Routing
import io.ktor.util.AttributeKey

class PathNormalizeFeature private constructor() {

    class Configuration {

    }

    companion object Feature : ApplicationFeature<Application, Configuration, PathNormalizeFeature> {
        override val key: AttributeKey<PathNormalizeFeature> = AttributeKey("PathNormalizeFeature")

        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit
        ): PathNormalizeFeature {
            val feature = PathNormalizeFeature()

            pipeline.environment.monitor.subscribe(Routing.RoutingCallStarted) {
                it.attributes.put(ROUTE, it.route)
                it.attributes.put(PATH, it.route.toString())
            }

            return feature
        }
    }
}