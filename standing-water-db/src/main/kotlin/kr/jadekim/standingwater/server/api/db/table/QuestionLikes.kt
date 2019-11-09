package kr.jadekim.standingwater.server.api.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.joda.time.DateTime

object QuestionLikes : IntIdTable("question_like") {
    val question = reference("question_id", Questions)
    val user = reference("user_id", Users)
    val createdAt = datetime("created_at").default(DateTime.now())
}