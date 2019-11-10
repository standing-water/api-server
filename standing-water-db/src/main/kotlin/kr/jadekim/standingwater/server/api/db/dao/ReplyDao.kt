package kr.jadekim.standingwater.server.api.db.dao

import kr.jadekim.db.DB
import kr.jadekim.standingwater.domain.Reply
import kr.jadekim.standingwater.enumuration.OrderType
import kr.jadekim.standingwater.server.api.db.table.Questions
import kr.jadekim.standingwater.server.api.db.table.Replies
import kr.jadekim.standingwater.server.api.db.table.ReplyLikes
import kr.jadekim.standingwater.server.api.db.table.Users
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime

class ReplyDao(
    private val db: DB
) {

    suspend fun getFiltered(
        questionId: Int,
        userId: Int,
        orderBy: OrderType,
        orderDesc: Boolean
    ) = db.read {
        val orderColumn = when (orderBy) {
            OrderType.LIKE_COUNT -> Count(ReplyLikes.id)
            OrderType.TIME -> Replies.createdAt
        }

        val replies = Replies
            .join(Users, JoinType.INNER, additionalConstraint = { Replies.creator eq Users.id })
            .join(ReplyLikes, JoinType.LEFT, additionalConstraint = { Replies.id eq ReplyLikes.reply })
            .slice(
                Replies.id,
                Replies.question,
                Replies.content,
                Replies.createdAt,
                Replies.creator,
                Users.nickname,
                Count(ReplyLikes.id)
            )
            .select {
                (Replies.question eq EntityID(questionId, Questions))
                    .and(Replies.isActive eq true)
            }
            .orderBy(orderColumn, if (orderDesc) SortOrder.DESC else SortOrder.ASC)

        val likes = ReplyLikes.slice(ReplyLikes.reply)
            .select {
                (ReplyLikes.user eq EntityID(userId, Users))
                    .and(ReplyLikes.reply inList replies.map { it[Replies.id] })
            }
            .map { it[ReplyLikes.reply] }

        replies.map { it.toDomainModel(it[Replies.id] in likes) }
    }

    suspend fun getMultiple(
        questionIdList: List<Int>,
        userId: Int,
        orderBy: OrderType,
        orderDesc: Boolean,
        limit: Int
    ) = db.read {
        val orderColumn = when (orderBy) {
            OrderType.LIKE_COUNT -> Count(ReplyLikes.id)
            OrderType.TIME -> Replies.createdAt
        }

        val replies = Replies
            .join(Users, JoinType.INNER, additionalConstraint = { Replies.creator eq Users.id })
            .join(ReplyLikes, JoinType.LEFT, additionalConstraint = { Replies.id eq ReplyLikes.reply })
            .slice(
                Replies.id,
                Replies.question,
                Replies.content,
                Replies.createdAt,
                Replies.creator,
                Users.nickname,
                Count(ReplyLikes.id)
            )
            .select {
                (Replies.question inList questionIdList.map { EntityID(it, Questions) })
                    .and(Replies.isActive eq true)
            }
            .groupBy(Replies.id)
            .orderBy(orderColumn, if (orderDesc) SortOrder.DESC else SortOrder.ASC)

        val likes = ReplyLikes.slice(ReplyLikes.reply)
            .select {
                (ReplyLikes.user eq EntityID(userId, Users))
                    .and(ReplyLikes.reply inList replies.map { it[Replies.id] })
            }
            .map { it[ReplyLikes.reply] }

        val count = replies.count()
        val result = replies.limit(limit)
            .map { it.toDomainModel(it[Replies.id] in likes) }

        Pair(count, result)
    }

    suspend fun add(questionId: Int, userId: Int, content: String) = db.execute {
        Replies.insertAndGetId {
            it[question] = EntityID(questionId, Questions)
            it[creator] = EntityID(userId, Users)
            it[this.content] = content
        }.value
    }

    suspend fun modifyContent(replyId: Int, content: String) = db.execute {
        Replies.update(
            where = { Replies.id eq replyId },
            body = {
                it[this.content] = content
                it[updatedAt] = DateTime.now()
            }
        )
    }

    suspend fun delete(replyId: Int) = db.execute {
        Replies.update(
            where = { Replies.id eq replyId },
            body = {
                it[isActive] = false
                it[updatedAt] = DateTime.now()
            }
        )
    }

    suspend fun addLikeReply(replyId: Int, userId: Int) = db.execute {
        ReplyLikes.insert {
            it[this.reply] = EntityID(replyId, Replies)
            it[this.user] = EntityID(userId, Users)
        }
    }

    suspend fun deleteLikeReply(replyId: Int, userId: Int) = db.execute {
        ReplyLikes.deleteWhere {
            (ReplyLikes.reply eq replyId)
                .and(ReplyLikes.user eq userId)
        }
    }

    suspend fun getLikeCount(replyId: Int) = db.read {
        ReplyLikes.select {
            ReplyLikes.reply eq replyId
        }
            .count()
    }

    private fun ResultRow.toDomainModel(isLiked: Boolean) = Reply(
        get(Replies.id).value,
        get(Replies.question).value,
        get(Replies.creator).value,
        get(Users.nickname),
        get(Replies.content),
        isLiked,
        get(Count(ReplyLikes.id))
    )
}