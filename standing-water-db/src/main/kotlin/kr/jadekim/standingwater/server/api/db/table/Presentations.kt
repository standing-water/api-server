package kr.jadekim.standingwater.server.api.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.joda.time.DateTime

object Presentations : IntIdTable("presentation") {
    val enterId = varchar("enter_id", 16).uniqueIndex()
    val fileId = uuid("file_id").uniqueIndex()
    val name = varchar("name", 64)
    val isActive = bool("is_active").default(true)
    val createdAt = datetime("created_at").default(DateTime.now())
}