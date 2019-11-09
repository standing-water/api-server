package kr.jadekim.standingwater.server.api.presenter.router

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readBytes
import io.ktor.util.AttributeKey
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kr.jadekim.standingwater.domain.Event
import kr.jadekim.standingwater.server.api.base.util.Jackson
import kr.jadekim.standingwater.service.RealtimeService
import org.slf4j.LoggerFactory

private val ATTRIBUTE_PRESENTATION_ID = AttributeKey<Int>("ws-realtime_presentation_id")
private val ATTRIBUTE_TOKEN = AttributeKey<String>("ws-realtime_token")

private val errorLogger = LoggerFactory.getLogger("ErrorLogger")

internal suspend fun DefaultWebSocketServerSession.handleWebSocket(realtimeService: RealtimeService) {
    suspend fun requestMessage(message: JsonNode) {
        when (message["message"].asText()) {
            "ping" -> outgoing.send(realtimeService.ping())
            "subscribe" -> {
                if (call.attributes.getOrNull(ATTRIBUTE_PRESENTATION_ID) != null) {
                    outgoing.send(Event.error("Already subscribed"))
                    return
                }

                val param = message["parameter"]
                val presentationId = param?.get("presentation_id")?.asInt()
                val token = param?.get("token")?.asText()

                if (presentationId == null || token.isNullOrBlank()) {
                    outgoing.send(Event.error("Invalid parameter"))
                    return
                }

                val subscription = try {
                    realtimeService.subscribe(presentationId, token)
                } catch (e: Exception) {
                    errorLogger.error(e.message, e)
                    outgoing.send(Event.error("Occur Error"))
                    return
                }

                launch(Dispatchers.IO) {
                    for (event in subscription) {
                        outgoing.sendText(event.first)
                    }
                }

                call.attributes.put(ATTRIBUTE_PRESENTATION_ID, presentationId)
                call.attributes.put(ATTRIBUTE_TOKEN, token)

                outgoing.send(Event.response(true))
            }
            "unsubscribe" -> {
                val presentationId = call.attributes.getOrNull(ATTRIBUTE_PRESENTATION_ID)
                val token = call.attributes.getOrNull(ATTRIBUTE_TOKEN)

                if (presentationId != null && token != null) {
                    val result = try {
                        realtimeService.unsubscribe(presentationId, token)
                    } catch (e: Exception) {
                        errorLogger.error(e.message, e)
                        outgoing.send(Event.error("Occur Error"))
                        return
                    }

                    if (result) {
                        close(CloseReason(CloseReason.Codes.NORMAL, Event.response(true).asJson()))
                    } else {
                        outgoing.send(Event.error("Not subscribed"))
                    }
                    return
                }

                outgoing.send(Event.error("Not subscribed"))
            }
        }
    }

    for (frame in incoming) {
        when (frame) {
            is Frame.Text -> requestMessage(frame.read())
            is Frame.Close -> {
                val presentationId = call.attributes.getOrNull(ATTRIBUTE_PRESENTATION_ID)
                val token = call.attributes.getOrNull(ATTRIBUTE_TOKEN)

                if (presentationId != null && token != null) {
                    try {
                        realtimeService.unsubscribe(presentationId, token)
                    } catch (e: Exception) {
                        errorLogger.error(e.message, e)
                    }
                }
            }
        }
    }
}

private fun Frame.read() = Jackson.mapper.readTree(readBytes())

private suspend fun SendChannel<Frame>.send(event: Event) = send(Frame.Text(event.asJson()))

private suspend fun SendChannel<Frame>.sendText(text: String) = send(Frame.Text(text))