package kr.jadekim.standingwater.service.protocol

data class CreatePresentationResponse(
    val id: Int,
    val enterId: String,
    val fileUrl: String,
    val token: String
)