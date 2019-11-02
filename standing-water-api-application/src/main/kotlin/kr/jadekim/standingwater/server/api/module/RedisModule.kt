package kr.jadekim.standingwater.server.api.module

import kr.jadekim.ext.getString
import kr.jadekim.redis.Redis
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.onClose

val RedisModule = module {
    single(named("standingwater-redis")) {
        Redis(getString("redis.standingwater.host")!!)
    }.onClose { it?.close() }
}