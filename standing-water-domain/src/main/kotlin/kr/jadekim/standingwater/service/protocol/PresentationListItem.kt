package kr.jadekim.standingwater.service.protocol

data class PresentationListItem(
    val id: Int,
    val enterId: String,
    val fileUrl: String,
    val name: String
)