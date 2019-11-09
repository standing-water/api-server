package kr.jadekim.standingwater.domain

import kr.jadekim.standingwater.enumuration.EventType
import kr.jadekim.standingwater.server.api.base.util.Jackson

data class Event(
    val event: EventType,
    val data: Map<String, Any> = emptyMap()
) {

    companion object {

        fun pong() = Event(EventType.PONG)

        fun response(result: Boolean, message: String = "") = Event(
            EventType.RESPONSE, mapOf(
                "result" to result,
                "message" to message
            )
        )

        fun error(reason: String) = response(false, reason)

        fun changePage(page: Int) = Event(EventType.CHANGE_PAGE, mapOf("page" to page))

        fun chatMessage(nickname: String, message: String) = Event(EventType.CHAT_MESSAGE, mapOf(
            "nickname" to nickname,
            "message" to message
        ))

        fun createQuestion(questionId: Int, nickname: String, page: Int, content: String) = Event(
            EventType.CRUD, mapOf(
                "type" to "create",
                "resource" to "question",
                "data" to mapOf(
                    "id" to questionId,
                    "nickname" to nickname,
                    "page" to page,
                    "content" to content
                )
            )
        )

        fun changeQuestionContent(questionId: Int, content: String) = Event(
            EventType.CRUD, mapOf(
                "type" to "update",
                "resource" to "question",
                "data" to mapOf("id" to questionId, "content" to content)
            )
        )

        fun changeQuestionLikeCount(questionId: Int, likeCount: Int) = Event(
            EventType.CRUD, mapOf(
                "type" to "update",
                "resource" to "question_like",
                "data" to mapOf("id" to questionId, "count" to likeCount)
            )
        )

        fun deleteQuestion(questionId: Int) = Event(
            EventType.CRUD, mapOf(
                "type" to "delete",
                "resource" to "question",
                "data" to mapOf("id" to questionId)
            )
        )

        fun createReply(replyId: Int, questionId: Int, nickname: String, content: String) = Event(
            EventType.CRUD, mapOf(
                "type" to "create",
                "resource" to "reply",
                "data" to mapOf(
                    "id" to replyId,
                    "questionId" to questionId,
                    "nickname" to nickname,
                    "content" to content
                )
            )
        )

        fun changeReplyContent(replyId: Int, content: String) = Event(
            EventType.CRUD, mapOf(
                "type" to "update",
                "resource" to "reply",
                "data" to mapOf("id" to replyId, "content" to content)
            )
        )

        fun changeReplyLikeCount(replyId: Int, likeCount: Int) = Event(
            EventType.CRUD, mapOf(
                "type" to "update",
                "resource" to "reply_like",
                "data" to mapOf("id" to replyId, "count" to likeCount)
            )
        )

        fun deleteReply(replyId: Int) = Event(
            EventType.CRUD, mapOf(
                "type" to "delete",
                "resource" to "reply",
                "data" to mapOf("id" to replyId)
            )
        )
    }

    fun asJson() = Jackson.mapper.writeValueAsString(this)
}