package kr.jadekim.standingwater.server.api.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.joda.time.DateTime

object ReplyLikes : IntIdTable("reply_like") {
    val reply = reference("reply_id", Replies)
    val user = reference("user_id", Users)
    val createdAt = datetime("created_at").default(DateTime.now())
}