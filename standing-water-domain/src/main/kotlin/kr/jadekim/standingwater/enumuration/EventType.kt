package kr.jadekim.standingwater.enumuration

import com.fasterxml.jackson.annotation.JsonValue

enum class EventType {
    PONG,
    RESPONSE,
    ERROR,
    CHANGE_PAGE,
    ACTIVE_USER,
    CHAT_MESSAGE,
    CRUD;

    @JsonValue
    val serializeValue = name.toLowerCase()
}