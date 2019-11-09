package kr.jadekim.standingwater.repository

import kr.jadekim.standingwater.domain.Question
import kr.jadekim.standingwater.enumuration.OrderType

interface QuestionRepository {

    suspend fun getFiltered(
        presentationId: Int,
        userId: Int,
        page: Int?,
        orderBy: OrderType,
        orderDesc: Boolean
    ): List<Question>

    suspend fun add(presentationId: Int, userId: Int, page: Int, content: String): Int

    suspend fun modifyContent(questionId: Int, content: String)

    suspend fun delete(questionId: Int)

    suspend fun addLikeQuestion(questionId: Int, userId: Int)

    suspend fun deleteLikeQuestion(questionId: Int, userId: Int)

    suspend fun getLikeCount(questionId: Int): Int
}