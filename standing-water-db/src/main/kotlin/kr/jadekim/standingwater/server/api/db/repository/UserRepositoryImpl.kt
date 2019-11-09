package kr.jadekim.standingwater.server.api.db.repository

import kr.jadekim.standingwater.repository.UserRepository
import kr.jadekim.standingwater.server.api.db.dao.UserDao
import java.util.*

class UserRepositoryImpl(
    private val dao: UserDao
) : UserRepository {

    override suspend fun add(presentationId: Int, nickname: String, authToken: UUID, isPresenter: Boolean) {
        dao.add(presentationId, nickname, authToken, isPresenter)
    }

    override suspend fun getByToken(token: UUID) = dao.get(token)

    override suspend fun getNickname(userId: Int): String  = dao.getNickname(userId)
}