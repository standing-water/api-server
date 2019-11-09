package kr.jadekim.standingwater.domain

data class Question(
    val id: Int,
    val page: Int,
    val creatorId: Int,
    val creatorNickname: String,
    val content: String,
    val isLiked: Boolean,
    val likeCount: Int
)