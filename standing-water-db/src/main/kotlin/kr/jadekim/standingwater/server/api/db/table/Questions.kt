package kr.jadekim.standingwater.server.api.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.joda.time.DateTime

object Questions : IntIdTable("question") {
    val presentation = reference("presentation_id", Presentations)
    val page = integer("page")
    val content = varchar("content", 512)
    val creator = reference("creator_id", Users)
    val isActive = bool("is_active").default(true)
    val createdAt = datetime("created_at").default(DateTime.now())
    val updatedAt = datetime("updated_at").default(DateTime.now())
}