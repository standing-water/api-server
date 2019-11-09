package kr.jadekim.standingwater.repository

import kr.jadekim.standingwater.domain.User
import java.util.*

interface UserRepository {

    suspend fun add(presentationId: Int, nickname: String, authToken: UUID, isPresenter: Boolean = false)

    suspend fun getByToken(token: UUID): User?

    suspend fun getNickname(userId: Int): String
}