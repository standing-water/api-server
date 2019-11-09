package kr.jadekim.standingwater.server.api.db.repository

import kr.jadekim.standingwater.enumuration.OrderType
import kr.jadekim.standingwater.repository.ReplyRepository
import kr.jadekim.standingwater.server.api.db.dao.ReplyDao

class ReplyRepositoryImpl(
    private val dao: ReplyDao
) : ReplyRepository {

    override suspend fun getFiltered(
        questionId: Int,
        userId: Int,
        orderBy: OrderType,
        orderDesc: Boolean
    ) = dao.getFiltered(questionId, userId, orderBy, orderDesc)

    override suspend fun getMultiple(
        questionIdList: List<Int>,
        userId: Int,
        orderBy: OrderType,
        orderDesc: Boolean,
        limit: Int
    ) = dao.getMultiple(questionIdList, userId, orderBy, orderDesc, limit)

    override suspend fun add(questionId: Int, userId: Int, content: String) = dao.add(questionId, userId, content)

    override suspend fun modifyContent(replyId: Int, content: String) {
        dao.modifyContent(replyId, content)
    }

    override suspend fun delete(replyId: Int) {
        dao.delete(replyId)
    }

    override suspend fun addLikeReply(replyId: Int, userId: Int) {
        dao.addLikeReply(replyId, userId)
    }

    override suspend fun deleteLikeReply(replyId: Int, userId: Int) {
        dao.deleteLikeReply(replyId, userId)
    }

    override suspend fun getLikeCount(replyId: Int): Int = dao.getLikeCount(replyId)
}