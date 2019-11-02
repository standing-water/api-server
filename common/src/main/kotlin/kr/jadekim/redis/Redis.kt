package kr.jadekim.redis

import io.lettuce.core.*
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.resource.DefaultClientResources
import io.lettuce.core.support.AsyncConnectionPoolSupport
import io.lettuce.core.support.BoundedPoolConfig
import io.netty.buffer.ByteBuf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.nio.ByteBuffer

class Redis(
    private val host: String,
    private val port: Int = 6379,
    private val dbIndex: Int = 0,
    private val keyPrefix: String = "",
    private val poolSize: Int = Runtime.getRuntime().availableProcessors() * 2
) : Closeable {

    private val uri = RedisURI.Builder.redis(host, port).withDatabase(dbIndex).build()
    private val resourceConfig = DefaultClientResources.create()
    private val options = ClientOptions.builder()
        .autoReconnect(true)
        .build()

    private val client = RedisClient.create(resourceConfig, uri).apply {
        options = this@Redis.options
    }

    private val poolConfig = BoundedPoolConfig.builder().minIdle(1)
        .maxIdle(poolSize)
        .maxTotal(poolSize)
        .testOnAcquire(true)
        .testOnCreate(true)
        .build()

    private val pool = AsyncConnectionPoolSupport.createBoundedObjectPool(
        { client.connectAsync(KeyPrefixStringCodec(keyPrefix), uri) },
        poolConfig
    )

    override fun close() {
        runBlocking { closeAsync() }
    }

    suspend fun closeAsync() {
        pool.closeAsync().asDeferred().await()
        client.shutdownAsync().asDeferred().await()
    }

    suspend fun ping() = execute { ping() }

    suspend fun set(key: String, value: String, expire: Int? = null) {
        pipe {
            it.add(set(key, value))

            if (expire != null) {
                it.add(expire(key, expire.toLong()))
            }
        }
    }

    suspend fun get(key: String): String? {
        return read { get(key) }
    }

    suspend fun set(vararg pairs: Pair<String, String>, expire: Int? = null) {
        val data = pairs.map { Pair(it.first, it.second) }

        pipe {
            it.add(mset(data.toMap()))

            if (expire != null) {
                it.addAll(data.map { each -> expire(each.first, expire.toLong()) })
            }
        }
    }

    suspend fun get(vararg keys: String): Map<String, String?> {
        val realKeys = keys.map { it }.toTypedArray()

        return readCommand {
            mget(*realKeys).asDeferred().await()
                .map { Pair(it.key, it.value) }
                .toMap()
        }
    }

    suspend fun getAndSet(key: String, value: String): String? {
        return execute { getset(key, value) }
    }

    suspend fun mset(values: Map<String, String>) {
        if (values.isEmpty()) {
            return
        }

        execute { mset(values) }
    }

    suspend fun mget(keys: List<String>): Map<String, String> {
        if (keys.isEmpty()) {
            return emptyMap()
        }

        return read { mget(*keys.toTypedArray()) }
            .map { Pair(it.key, it.getValueOrElse("")) }
            .toMap()
    }

    suspend fun delete(vararg key: String) {
        pipe {
            it.addAll(key.map { each -> del(each) })
        }
    }

    suspend fun exists(key: String): Boolean {
        return read { exists(key) } > 0
    }

    suspend fun expire(key: String, expire: Int) {
        execute { expire(key, expire.toLong()) }
    }

    suspend fun lpush(key: String, vararg value: String) {
        execute { lpush(key, *value) }
    }

    suspend fun lrange(key: String, start: Long, end: Long): List<String> {
        return read { lrange(key, start, end) }
    }

    suspend fun lpop(key: String): String? {
        return execute { lpop(key) }
    }

    suspend fun brpop(key: String, timeout: Long = 0): String? {
        return execute { brpop(timeout, key) }?.value
    }

    suspend fun rpop(key: String): String? {
        return execute { rpop(key) }
    }

    suspend fun lindex(key: String): String? {
        return read { lindex(key, -1) }?.let { it }
    }

    suspend fun llen(key: String): Long {
        return read { llen(key) }
    }

    suspend fun zcard(key: String): Long {
        return read { zcard(key) }
    }

    suspend fun zrange(key: String, start: Long, stop: Long): List<String> {
        return read { zrange(key, start, stop) }
    }

    suspend fun zadd(key: String, score: Double, value: String) {
        execute { zadd(key, score, value) }
    }

    suspend fun zadd(key: String, vararg scoreAndValue: ScoredValue<String>) {
        execute { zadd(key, *scoreAndValue) }
    }

    suspend fun pubsub(): RedisPubSubConnection {
        return RedisPubSubConnection(
            client.connectPubSubAsync(StringCodec.UTF8, uri)
                .asDeferred()
                .await()
        )
    }

    internal suspend fun <T> execute(statement: RedisAsyncCommands<String, String>.() -> RedisFuture<T>): T {
        return executeCommand { statement().asDeferred().await() }
    }

    internal suspend fun <T> read(statement: RedisAsyncCommands<String, String>.() -> RedisFuture<T>): T {
        return readCommand { statement().asDeferred().await() }
    }

    internal suspend fun pipe(statement: suspend RedisAsyncCommands<String, String>.(MutableList<RedisFuture<*>>) -> Unit): List<Any> {
        val commands = mutableListOf<RedisFuture<*>>()

        return executeCommand {
            setAutoFlushCommands(false)

            statement(commands)

            flushCommands()

            setAutoFlushCommands(true)

            val deferredCommands = commands.map { it.asDeferred() }
                .toTypedArray()

            awaitAll(*deferredCommands)
        }
    }

    private suspend fun <T> executeCommand(statement: suspend RedisAsyncCommands<String, String>.() -> T): T {
        return withContext(Dispatchers.IO) {
            val connection = pool.acquire().asDeferred().await()

            val result = connection.async().statement()

            pool.release(connection)

            result
        }
    }

    private suspend fun <T> readCommand(statement: suspend RedisAsyncCommands<String, String>.() -> T): T {
        return executeCommand(statement)
    }

    private class KeyPrefixStringCodec(val keyPrefix: String) : StringCodec(Charsets.UTF_8) {

        override fun encodeKey(key: String?): ByteBuffer {
            return super.encodeKey(keyPrefix + key)
        }

        override fun encodeKey(key: String?, target: ByteBuf?) {
            super.encodeKey(keyPrefix + key, target)
        }
    }
}