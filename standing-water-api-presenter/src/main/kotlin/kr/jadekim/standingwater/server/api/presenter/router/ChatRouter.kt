package kr.jadekim.standingwater.server.api.presenter.router

import io.ktor.routing.Route
import io.ktor.routing.post
import kr.jadekim.ktor.bodyParam
import kr.jadekim.ktor.success
import kr.jadekim.standingwater.server.api.presenter.ext.presentationId
import kr.jadekim.standingwater.server.api.presenter.ext.user
import kr.jadekim.standingwater.service.ChatService

fun Route.chat(chatService: ChatService) {
    post {
        chatService.sendMessage(presentationId, user, bodyParam("message"))

        success()
    }
}