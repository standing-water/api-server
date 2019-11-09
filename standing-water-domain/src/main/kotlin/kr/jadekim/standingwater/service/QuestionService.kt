package kr.jadekim.standingwater.service

import kr.jadekim.standingwater.domain.Event
import kr.jadekim.standingwater.domain.alias.PublishChannel
import kr.jadekim.standingwater.enumuration.OrderType
import kr.jadekim.standingwater.repository.QuestionRepository
import kr.jadekim.standingwater.repository.ReplyRepository
import kr.jadekim.standingwater.repository.UserRepository
import kr.jadekim.standingwater.service.protocol.ListResponse
import kr.jadekim.standingwater.service.protocol.Question
import kr.jadekim.standingwater.service.protocol.Reply

class QuestionService(
    private val questionRepository: QuestionRepository,
    private val replyRepository: ReplyRepository,
    private val userRepository: UserRepository,
    private val publishChannel: PublishChannel
) {

    suspend fun getQuestions(
        presentationId: Int,
        userId: Int,
        page: Int?,
        orderBy: OrderType?,
        orderDesc: Boolean?
    ): ListResponse<Question> {
        val realOrderBy = orderBy ?: OrderType.TIME
        val realOrderDesc = orderDesc ?: true

        val questions = questionRepository.getFiltered(
            presentationId,
            userId,
            page,
            realOrderBy,
            realOrderDesc
        )

        val replies = replyRepository.getMultiple(questions.map { it.id }, userId, realOrderBy, realOrderDesc, 3)
        val replyItems = replies.second
            .map {
                Reply(
                    it.id,
                    it.questionId,
                    it.creatorNickname,
                    it.content,
                    it.creatorId == userId,
                    it.isLiked,
                    it.likeCount
                )
            }
            .groupBy { it.questionId }

        return ListResponse(
            questions.map {
                Question(
                    it.id,
                    it.page,
                    it.creatorNickname,
                    it.content,
                    it.creatorId == userId,
                    it.isLiked,
                    it.likeCount,
                    ListResponse(replyItems[it.id] ?: emptyList(), replies.first)
                )
            }
        )
    }

    suspend fun registerQuestion(presentationId: Int, userId: Int, page: Int, content: String): Int {
        val id = questionRepository.add(presentationId, userId, page, content)
        val nickname = userRepository.getNickname(userId)

        publishChannel.send(presentationId to Event.createQuestion(id, nickname, page, content))

        return id
    }

    suspend fun modifyQuestionContent(presentationId: Int, questionId: Int, content: String) {
        questionRepository.modifyContent(questionId, content)

        publishChannel.send(presentationId to Event.changeQuestionContent(questionId, content))
    }

    suspend fun deleteQuestion(presentationId: Int, questionId: Int) {
        questionRepository.delete(questionId)

        publishChannel.send(presentationId to Event.deleteQuestion(questionId))
    }

    suspend fun likeQuestion(presentationId: Int, questionId: Int, userId: Int) {
        questionRepository.addLikeQuestion(questionId, userId)

        val likeCount = questionRepository.getLikeCount(questionId)

        publishChannel.send(presentationId to Event.changeQuestionLikeCount(questionId, likeCount))
    }

    suspend fun unlikeQuestion(presentationId: Int, questionId: Int, userId: Int) {
        questionRepository.deleteLikeQuestion(questionId, userId)

        val likeCount = questionRepository.getLikeCount(questionId)

        publishChannel.send(presentationId to Event.changeQuestionLikeCount(questionId, likeCount))
    }
}