package kr.jadekim.standingwater.service

import kr.jadekim.standingwater.domain.Event
import kr.jadekim.standingwater.domain.User
import kr.jadekim.standingwater.domain.alias.PublishChannel

class ChatService(
    private val publishChannel: PublishChannel
) {

    fun sendMessage(presentationId: Int, user: User, message: String) {
        publishChannel.offer(presentationId to Event.chatMessage(user.nickname, message))
    }
}