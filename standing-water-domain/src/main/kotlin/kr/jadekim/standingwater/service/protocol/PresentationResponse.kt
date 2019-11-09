package kr.jadekim.standingwater.service.protocol

data class PresentationResponse(
    val id: Int,
    val enterId: String,
    val fileUrl: String,
    val name: String,
    val currentPage: Int
)