package kr.jadekim.standingwater.service.protocol

data class Question(
    val id: Int,
    val page: Int,
    val nickname: String,
    val content: String,
    val isMyQuestion: Boolean,
    val isLiked: Boolean,
    val likeCount: Int,
    val reply: ListResponse<Reply>
)