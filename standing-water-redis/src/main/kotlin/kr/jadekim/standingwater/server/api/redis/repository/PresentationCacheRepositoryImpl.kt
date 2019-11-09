package kr.jadekim.standingwater.server.api.redis.repository

import kr.jadekim.redis.Redis
import kr.jadekim.standingwater.repository.PresentationCacheRepository

class PresentationCacheRepositoryImpl(
    private val redis: Redis
) : PresentationCacheRepository {

    override suspend fun setCurrentPage(presentationId: Int, page: Int) {
        redis.set("CURRENT_PAGE:$presentationId", page.toString())
    }

    override suspend fun getCurrentPage(presentationId: Int): Int? {
        return redis.get("CURRENT_PAGE:$presentationId")?.toIntOrNull()
    }
}