package kr.jadekim.standingwater.repository

import kr.jadekim.standingwater.domain.Reply
import kr.jadekim.standingwater.enumuration.OrderType

interface ReplyRepository {

    suspend fun getFiltered(questionId: Int, userId: Int, orderBy: OrderType, orderDesc: Boolean): List<Reply>

    suspend fun getMultiple(
        questionIdList: List<Int>,
        userId: Int,
        orderBy: OrderType,
        orderDesc: Boolean,
        limit: Int
    ): Pair<Int, List<Reply>>

    suspend fun add(questionId: Int, userId: Int, content: String): Int

    suspend fun modifyContent(replyId: Int, content: String)

    suspend fun delete(replyId: Int)

    suspend fun addLikeReply(replyId: Int, userId: Int)

    suspend fun deleteLikeReply(replyId: Int, userId: Int)

    suspend fun getLikeCount(replyId: Int): Int
}