package kr.jadekim.standingwater.server.api.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.joda.time.DateTime

object Replies : IntIdTable("reply") {
    val question = reference("question_id", Questions)
    val content = varchar("content", 512)
    val creator = reference("creator_id", Users)
    val isActive = bool("is_active").default(true)
    val createdAt = datetime("created_at").default(DateTime.now())
    val updatedAt = datetime("updated_at").default(DateTime.now())
}