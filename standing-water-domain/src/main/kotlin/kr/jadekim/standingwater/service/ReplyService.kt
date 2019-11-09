package kr.jadekim.standingwater.service

import kr.jadekim.standingwater.domain.Event
import kr.jadekim.standingwater.domain.alias.PublishChannel
import kr.jadekim.standingwater.enumuration.OrderType
import kr.jadekim.standingwater.repository.ReplyRepository
import kr.jadekim.standingwater.repository.UserRepository
import kr.jadekim.standingwater.service.protocol.ListResponse
import kr.jadekim.standingwater.service.protocol.Reply

class ReplyService(
    private val replyRepository: ReplyRepository,
    private val userRepository: UserRepository,
    private val publishChannel: PublishChannel
) {

    suspend fun getReplies(
        questionId: Int,
        userId: Int,
        orderBy: OrderType?,
        orderDesc: Boolean?
    ): ListResponse<Reply> {
        val realOrderBy = orderBy ?: OrderType.TIME
        val realOrderDesc = orderDesc ?: true

        val replies = replyRepository.getFiltered(questionId, userId, realOrderBy, realOrderDesc)
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

        return ListResponse(replies)
    }

    suspend fun registerReply(presentationId: Int, questionId: Int, userId: Int, content: String): Int {
        val id = replyRepository.add(questionId, userId, content)
        val nickname = userRepository.getNickname(userId)

        publishChannel.send(presentationId to Event.createReply(id, questionId, nickname, content))

        return id
    }

    suspend fun modifyReplyContent(presentationId: Int, replyId: Int, content: String) {
        replyRepository.modifyContent(replyId, content)

        publishChannel.send(presentationId to Event.changeReplyContent(replyId, content))
    }

    suspend fun deleteReply(presentationId: Int, replyId: Int) {
        replyRepository.delete(replyId)

        publishChannel.send(presentationId to Event.deleteReply(replyId))
    }

    suspend fun likeReply(presentationId: Int, replyId: Int, userId: Int) {
        replyRepository.addLikeReply(replyId, userId)

        val likeCount = replyRepository.getLikeCount(replyId)

        publishChannel.send(presentationId to Event.changeReplyLikeCount(replyId, likeCount))
    }

    suspend fun unlikeReply(presentationId: Int, replyId: Int, userId: Int) {
        replyRepository.deleteLikeReply(replyId, userId)

        val likeCount = replyRepository.getLikeCount(replyId)

        publishChannel.send(presentationId to Event.changeReplyLikeCount(replyId, likeCount))
    }
}