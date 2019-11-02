package kr.jadekim.standingwater.server.api.presenter

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import kr.jadekim.enumuration.ServiceEnvironment
import kr.jadekim.ext.getInt
import kr.jadekim.ext.getString
import kr.jadekim.ktor.BaseKtorServer
import kr.jadekim.standingwater.server.api.base.util.Jackson
import java.util.*

class NFAuthApiServer(
    serviceEnv: ServiceEnvironment,
    properties: Properties
) : BaseKtorServer(
    serviceEnv,
    properties.getInt("SERVICE_PORT", 8080),
    properties.getString("DEPLOY_VERSION", "not_set"),
    Jackson.mapper
) {

    override fun Routing.configureRoute() {
        route("/") {
            get("/health") { call.respondText("ok") }
        }
    }
}