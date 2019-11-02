package kr.jadekim.redis

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.jadekim.util.SuspendQueue

open class RedisQueue<T>(
    private val redis: Redis,
    val redisKey: String,
    private val typeRef: Class<T>
) : SuspendQueue<T> {

    protected val mapper = jacksonObjectMapper()

    override suspend fun size() = redis.llen(redisKey)

    override suspend fun isEmpty() = size() == 0L

    override suspend fun pop() = deserializeSafe(redis.rpop(redisKey))

    override suspend fun peek() = pop()

    override suspend fun push(value: T) {
        redis.lpush(redisKey, serialize(value))
    }

    override suspend fun push(values: List<T>) {
        val serialized = values.map { serialize(it) }.toTypedArray()

        redis.lpush(redisKey, *serialized)
    }

    override suspend fun clear() = redis.delete(redisKey)

    protected open fun serialize(data: T): String {
        return mapper.writeValueAsString(data)
    }

    protected fun deserializeSafe(data: String?): T? {
        if (data.isNullOrBlank()) {
            return null
        }

        return deserialize(data)
    }

    protected open fun deserialize(data: String): T {
        return mapper.readValue(data, typeRef)
    }
}