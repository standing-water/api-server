package kr.jadekim.standingwater.service

import kr.jadekim.exception.UnauthorizedException
import kr.jadekim.standingwater.domain.User
import kr.jadekim.standingwater.repository.UserRepository
import java.util.*

class UserService(
    private val userRepository: UserRepository
) {

    suspend fun getInfoByToken(token: String): User {
        return userRepository.getByToken(UUID.fromString(token)) ?: throw UnauthorizedException(token)
    }
}