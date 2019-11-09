package kr.jadekim.standingwater.server.api.db.repository

import kr.jadekim.standingwater.enumuration.OrderType
import kr.jadekim.standingwater.repository.QuestionRepository
import kr.jadekim.standingwater.server.api.db.dao.QuestionDao

class QuestionRepositoryImpl(
    private val dao: QuestionDao
) : QuestionRepository {

    override suspend fun getFiltered(
        presentationId: Int,
        userId: Int,
        page: Int?,
        orderBy: OrderType,
        orderDesc: Boolean
    ) = dao.getFiltered(presentationId, userId, page, orderBy, orderDesc)

    override suspend fun add(presentationId: Int, userId: Int, page: Int, content: String): Int {
        return dao.add(presentationId, userId, page, content)
    }

    override suspend fun modifyContent(questionId: Int, content: String) {
        dao.modifyContent(questionId, content)
    }

    override suspend fun delete(questionId: Int) {
        dao.delete(questionId)
    }

    override suspend fun addLikeQuestion(questionId: Int, userId: Int) {
        dao.addLikeQuestion(questionId, userId)
    }

    override suspend fun deleteLikeQuestion(questionId: Int, userId: Int) {
        dao.deleteLikeQuestion(questionId, userId)
    }

    override suspend fun getLikeCount(questionId: Int): Int = dao.getLikeCount(questionId)
}