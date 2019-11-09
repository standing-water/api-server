package kr.jadekim.standingwater.service.protocol

data class Reply(
    val id: Int,
    val questionId: Int,
    val nickname: String,
    val content: String,
    val isMyReply: Boolean,
    val isLiked: Boolean,
    val likeCount: Int
)