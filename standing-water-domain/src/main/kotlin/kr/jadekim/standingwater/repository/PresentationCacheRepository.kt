package kr.jadekim.standingwater.repository

interface PresentationCacheRepository {

    suspend fun setCurrentPage(presentationId: Int, page: Int)

    suspend fun getCurrentPage(presentationId: Int): Int?
}