package kr.jadekim.standingwater.server.api.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.joda.time.DateTime

object Users : IntIdTable("user") {
    val presentation = reference("presentation_id", Presentations)
    val nickname = varchar("nickname", 32)
    val authToken = uuid("auth_token")
    val isPresenter = bool("is_presenter").default(false)
    val createdAt = datetime("created_at").default(DateTime.now())
}