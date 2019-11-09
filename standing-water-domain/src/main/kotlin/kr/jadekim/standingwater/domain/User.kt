package kr.jadekim.standingwater.domain

import java.util.*
import kotlin.random.Random

data class User(
    val id: Int,
    val presentationId: Int,
    val nickname: String,
    val token: UUID,
    val isPresenter: Boolean
) {

    companion object {

        fun createRandomNickname(): String {
            val number = (System.currentTimeMillis() / 1000) % 10000
            val value = Random.nextInt(1, 1000)

            return "사용자$number-$value"
        }
    }
}