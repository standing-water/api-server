package kr.jadekim.standingwater.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kr.jadekim.exception.UnauthorizedException
import kr.jadekim.standingwater.domain.Event
import kr.jadekim.standingwater.domain.alias.PublishChannel
import kr.jadekim.standingwater.domain.alias.SubscribeChannel
import kr.jadekim.standingwater.server.api.base.util.Jackson
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

private typealias Subscriber = Channel<Pair<String, Event>>

class RealtimeService(
    private val userService: UserService,
    private val publishChannel: PublishChannel,
    private val subscribeChannel: SubscribeChannel
) : CoroutineScope {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val subscribers = mutableMapOf<Int, MutableList<Subscriber>>()
    private val channelMap = mutableMapOf<String, Subscriber>()

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    fun startup() {
        launch {
            for (event in subscribeChannel) {
                val serialized = Jackson.mapper.writeValueAsString(event.second)

                logger.info("Event Publish : ${event.first} - $serialized")

                subscribers[event.first]?.forEach {
                    it.offer(serialized to event.second)
                }

                logger.info("Event Published : ${event.first} - $serialized")
            }
        }
    }

    fun ping() = Event.pong()

    suspend fun subscribe(presentationId: Int, token: String): ReceiveChannel<Pair<String, Event>> {
        val user = userService.getInfoByToken(token)

        if (user.presentationId != presentationId) {
            throw UnauthorizedException(token)
        }

        val channel = Channel<Pair<String, Event>>()

        var list = subscribers[presentationId]

        if (list == null) {
            list = mutableListOf()
            subscribers[presentationId] = list
        }

        channelMap[token] = channel
        list.add(channel)
        publishChannel.send(presentationId to Event.activeUser(list.size))

        return channel
    }

    suspend fun unsubscribe(presentationId: Int, token: String): Boolean {
        val user = userService.getInfoByToken(token)

        if (user.presentationId != presentationId) {
            throw UnauthorizedException(token)
        }

        val channel = channelMap.remove(token) ?: return false
        val list = subscribers[presentationId]

        list?.remove(channel)
        publishChannel.send(presentationId to Event.activeUser(list?.size ?: 0))

        channel.close()

        return true
    }
}