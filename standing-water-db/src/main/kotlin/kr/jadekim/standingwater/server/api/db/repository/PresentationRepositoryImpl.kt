package kr.jadekim.standingwater.server.api.db.repository

import kr.jadekim.standingwater.domain.Presentation
import kr.jadekim.standingwater.repository.PresentationRepository
import kr.jadekim.standingwater.server.api.db.dao.PresentationDao
import java.util.*

class PresentationRepositoryImpl(
    private val dao: PresentationDao
) : PresentationRepository {

    override suspend fun getAll(): List<Presentation> = dao.getAll()

    override suspend fun get(enterId: String) = dao.get(enterId)

    override suspend fun add(enterId: String, fileId: UUID, name: String): Int {
        return dao.add(enterId, fileId, name)
    }
}