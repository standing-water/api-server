package kr.jadekim.standingwater.server.api.db.dao

import kr.jadekim.db.DB
import kr.jadekim.standingwater.domain.User
import kr.jadekim.standingwater.server.api.db.table.Presentations
import kr.jadekim.standingwater.server.api.db.table.Users
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

class UserDao(
    private val db: DB
) {

    suspend fun add(presentationId: Int, nickname: String, authToken: UUID, isPresenter: Boolean) = db.execute {
        Users.insert {
            it[this.presentation] = EntityID(presentationId, Presentations)
            it[this.nickname] = nickname
            it[this.authToken] = authToken
            it[this.isPresenter] = isPresenter
        }
    }

    suspend fun get(token: UUID) = db.read {
        Users.slice(Users.id, Users.presentation, Users.nickname, Users.authToken, Users.isPresenter)
            .select { Users.authToken eq token }
            .firstOrNull()?.toDomainModel()
    }

    suspend fun getNickname(userId: Int) = db.read {
        Users.slice(Users.nickname)
            .select { Users.id eq userId }
            .first()[Users.nickname]
    }

    private fun ResultRow.toDomainModel() = User(
        get(Users.id).value,
        get(Users.presentation).value,
        get(Users.nickname),
        get(Users.authToken),
        get(Users.isPresenter)
    )
}