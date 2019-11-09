package kr.jadekim.standingwater.domain

data class Reply(
    val id: Int,
    val questionId: Int,
    val creatorId: Int,
    val creatorNickname: String,
    val content: String,
    val isLiked: Boolean,
    val likeCount: Int
)