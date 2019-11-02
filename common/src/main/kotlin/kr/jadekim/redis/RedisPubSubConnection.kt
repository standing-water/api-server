package kr.jadekim.redis

import io.lettuce.core.pubsub.RedisPubSubListener
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import kotlin.coroutines.coroutineContext

class RedisPubSubConnection internal constructor(
    private val connection: StatefulRedisPubSubConnection<String, String>
) : Closeable {

    override fun close() {
        runBlocking { closeAsync() }
    }

    suspend fun closeAsync() {
        connection.closeAsync().asDeferred().await()
    }

    suspend fun subscribe(vararg channel: String) {
        connection.async().subscribe(*channel).asDeferred().await()
    }

    suspend fun unsubscribe(vararg channel: String) {
        connection.async().unsubscribe(*channel).asDeferred().await()
    }

    suspend fun psubscribe(vararg pattern: String) {
        connection.async().psubscribe(*pattern).asDeferred().await()
    }

    suspend fun punsubscribe(vararg pattern: String) {
        connection.async().punsubscribe(*pattern).asDeferred().await()
    }

    fun asFlow(): Flow<Pair<String, String>> = flow {
        val currentScope = CoroutineScope(coroutineContext)

        val listener = object : AbstractPubSubListener() {

            override fun message(channel: String, message: String?) {
                if (!currentScope.isActive) {
                    connection.removeListener(this)
                    return
                }

                currentScope.launch {
                    emit(Pair(channel, message ?: ""))
                }
            }

            override fun message(pattern: String, channel: String, message: String?) {
                if (!currentScope.isActive) {
                    connection.removeListener(this)
                    return
                }

                currentScope.launch {
                    emit(Pair(channel, message ?: ""))
                }
            }
        }

        connection.addListener(listener)
    }

    private abstract class AbstractPubSubListener : RedisPubSubListener<String, String> {

        override fun psubscribed(pattern: String, count: Long) {
            //do nothing
        }

        override fun punsubscribed(pattern: String, count: Long) {
            //do nothing
        }

        override fun unsubscribed(channel: String, count: Long) {
            //do nothing
        }

        override fun subscribed(channel: String, count: Long) {
            //do nothing
        }
    }
}