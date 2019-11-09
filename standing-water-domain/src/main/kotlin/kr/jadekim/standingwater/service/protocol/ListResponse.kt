package kr.jadekim.standingwater.service.protocol

data class ListResponse<T>(
    val items: List<T>,
    val count: Int = items.size
)