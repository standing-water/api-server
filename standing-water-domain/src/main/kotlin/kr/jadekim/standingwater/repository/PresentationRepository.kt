package kr.jadekim.standingwater.repository

import kr.jadekim.standingwater.domain.Presentation
import java.util.*

interface PresentationRepository {

    suspend fun getAll(): List<Presentation>

    suspend fun get(enterId: String): Presentation?

    suspend fun add(enterId: String, fileId: UUID, name: String): Int
}