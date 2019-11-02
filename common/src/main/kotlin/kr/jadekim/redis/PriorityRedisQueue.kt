package kr.jadekim.redis

import io.lettuce.core.ScoredValue

class PriorityRedisQueue<T>(
    private val redis: Redis,
    redisKey: String,
    typeRef: Class<T>
) : RedisQueue<T>(redis, redisKey, typeRef) {

    override suspend fun size(): Long = redis.zcard(redisKey)

    override suspend fun clear() = redis.delete(redisKey)

    @Suppress("UNCHECKED_CAST")
    override suspend fun pop(): T? {
        val result = redis.pipe {
            it.add(zrange(redisKey, 0, 0))
            it.add(zremrangebyrank(redisKey, 0, 0))
        }.first() as List<String>

        return deserializeSafe(result.firstOrNull())
    }

    override suspend fun peek(): T? {
        return deserializeSafe(redis.zrange(redisKey, 0, 0).firstOrNull())
    }

    override suspend fun push(value: T) {
        redis.zadd(redisKey, ScoredValue.just(100.0, serialize(value)))
    }

    override suspend fun push(values: List<T>) {
        val data = values.map { serialize(it) }
            .map { ScoredValue.just(100.0, it) }
            .toTypedArray()

        redis.zadd(redisKey, *data)
    }

    suspend fun push(value: T, score: Double) {
        redis.zadd(redisKey, score, serialize(value))
    }

    suspend fun push(vararg values: Pair<T, Double>) {
        val data = values
            .map { ScoredValue.just(it.second, serialize(it.first)) }
            .toTypedArray()

        redis.zadd(redisKey, *data)
    }

    suspend fun slice(start: Long, stop: Long): List<T> {
        return redis.zrange(redisKey, start, stop - 1).map { deserialize(it) }
    }

    suspend fun get(idx: Long) {
        redis.zrange(redisKey, idx, idx)
    }

    suspend fun elements(): List<T> {
        return redis.zrange(redisKey, 0, -1)
            .map { deserialize(it) }
    }

    suspend fun extend(collection: Collection<Pair<T, Double>>) {
        redis.pipe {
            it.addAll(
                collection.map { each -> zadd(redisKey, each.second, serialize(each.first)) }
            )
        }
    }
}