package kr.jadekim.standingwater.server.api.db.dao

import kr.jadekim.db.DB
import kr.jadekim.standingwater.domain.Question
import kr.jadekim.standingwater.enumuration.OrderType
import kr.jadekim.standingwater.server.api.db.table.Presentations
import kr.jadekim.standingwater.server.api.db.table.QuestionLikes
import kr.jadekim.standingwater.server.api.db.table.Questions
import kr.jadekim.standingwater.server.api.db.table.Users
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime

class QuestionDao(
    private val db: DB
) {

    suspend fun getFiltered(
        presentationId: Int,
        userId: Int,
        page: Int?,
        orderBy: OrderType,
        orderDesc: Boolean
    ) = db.read {
        val orderColumn = when (orderBy) {
            OrderType.LIKE_COUNT -> Count(QuestionLikes.id)
            OrderType.TIME -> Questions.createdAt
        }

        val questions = Questions
            .join(Users, JoinType.INNER, additionalConstraint = { Questions.creator eq Users.id })
            .join(QuestionLikes, JoinType.LEFT, additionalConstraint = { Questions.id eq QuestionLikes.question })
            .slice(
                Questions.id,
                Questions.page,
                Questions.content,
                Questions.createdAt,
                Questions.creator,
                Users.nickname,
                Count(QuestionLikes.id)
            )
            .select {
                var op = (Questions.presentation eq EntityID(presentationId, Presentations))
                    .and(Questions.isActive eq true)

                if (page != null) {
                    op = op.and(Questions.page eq page)
                }

                op
            }
            .groupBy(Questions.id)
            .orderBy(orderColumn, if (orderDesc) SortOrder.DESC else SortOrder.ASC)

        val likes = QuestionLikes.slice(QuestionLikes.question)
            .select {
                (QuestionLikes.user eq EntityID(userId, Users))
                    .and(QuestionLikes.question inList questions.map { it[Questions.id] })
            }
            .map { it[QuestionLikes.question] }

        questions.map { it.toDomainModel(it[Questions.id] in likes) }
    }

    suspend fun add(presentationId: Int, userId: Int, page: Int, content: String) = db.execute {
        Questions.insertAndGetId {
            it[presentation] = EntityID(presentationId, Presentations)
            it[creator] = EntityID(userId, Users)
            it[this.page] = page
            it[this.content] = content
        }.value
    }

    suspend fun modifyContent(questionId: Int, content: String) = db.execute {
        Questions.update(
            where = { Questions.id eq questionId },
            body = {
                it[this.content] = content
                it[updatedAt] = DateTime.now()
            }
        )
    }

    suspend fun delete(questionId: Int) = db.execute {
        Questions.update(
            where = { Questions.id eq questionId },
            body = {
                it[isActive] = false
                it[updatedAt] = DateTime.now()
            }
        )
    }

    suspend fun addLikeQuestion(questionId: Int, userId: Int) = db.execute {
        QuestionLikes.insert {
            it[this.question] = EntityID(questionId, Questions)
            it[this.user] = EntityID(userId, Users)
        }
    }

    suspend fun deleteLikeQuestion(questionId: Int, userId: Int) = db.execute {
        QuestionLikes.deleteWhere {
            (QuestionLikes.question eq questionId)
                .and(QuestionLikes.user eq userId)
        }
    }

    suspend fun getLikeCount(questionId: Int) = db.read {
        QuestionLikes.select {
            QuestionLikes.question eq questionId
        }
            .count()
    }

    private fun ResultRow.toDomainModel(isLiked: Boolean) = Question(
        get(Questions.id).value,
        get(Questions.page),
        get(Questions.creator).value,
        get(Users.nickname),
        get(Questions.content),
        isLiked,
        get(Count(QuestionLikes.id))
    )
}