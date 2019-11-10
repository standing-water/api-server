package kr.jadekim.standingwater.server.api.presenter

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.features.CORS
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.*
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.mapNotNull
import kr.jadekim.enumuration.ServiceEnvironment
import kr.jadekim.ext.getInt
import kr.jadekim.ext.getString
import kr.jadekim.ktor.BaseKtorServer
import kr.jadekim.standingwater.domain.Event
import kr.jadekim.standingwater.server.api.base.util.Jackson
import kr.jadekim.standingwater.server.api.presenter.authentication.UserPrincipal
import kr.jadekim.standingwater.server.api.presenter.authentication.bearer
import kr.jadekim.standingwater.server.api.presenter.router.*
import kr.jadekim.standingwater.server.api.presenter.router.handleWebSocket
import kr.jadekim.standingwater.service.*
import java.util.*

class StandingWaterApiServer(
    serviceEnv: ServiceEnvironment,
    properties: Properties,
    private val userService: UserService,
    private val fileService: FileService,
    private val presentationService: PresentationService,
    private val chatService: ChatService,
    private val questionService: QuestionService,
    private val replyService: ReplyService,
    private val realtimeService: RealtimeService
) : BaseKtorServer(
    serviceEnv,
    properties.getInt("SERVICE_PORT", 8080),
    properties.getString("DEPLOY_VERSION", "not_set"),
    Jackson.mapper
) {

    override fun Application.installExtraFeature() {
        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            method(HttpMethod.Patch)
            header(HttpHeaders.AccessControlAllowOrigin)
            header(HttpHeaders.AccessControlAllowHeaders)
            header(HttpHeaders.AccessControlAllowMethods)
            header(HttpHeaders.AccessControlRequestHeaders)
            header(HttpHeaders.AccessControlRequestMethod)
            header(HttpHeaders.ContentType)
            header(HttpHeaders.Authorization)
            allowCredentials = true
            anyHost()
        }

        install(Authentication) {
            bearer {
                validate {
                    UserPrincipal(it.token, userService.getInfoByToken(it.token))
                }
            }
        }

        install(WebSockets)
    }

    override fun Routing.configureRoute() {
        get("/health") { call.respondText("ok") }

        route("/presentation") {
            presentation(presentationService, fileService)

            route("/{presentationId}") {
                authenticate {
                    route("/chat") { chat(chatService) }

                    route("/question") {
                        question(questionService)

                        route("/{questionId}") {
                            route("/reply") {
                                reply(replyService)
                            }
                        }
                    }
                }
            }
        }

        webSocket("/realtime") { handleWebSocket(realtimeService) }
    }
}