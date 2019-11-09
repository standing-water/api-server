package kr.jadekim.standingwater.server.api.presenter.router

import io.ktor.routing.*
import kr.jadekim.ktor.bodyParam
import kr.jadekim.ktor.queryParamSafe
import kr.jadekim.ktor.response
import kr.jadekim.ktor.success
import kr.jadekim.standingwater.enumuration.OrderType
import kr.jadekim.standingwater.server.api.presenter.ext.presentationId
import kr.jadekim.standingwater.server.api.presenter.ext.questionId
import kr.jadekim.standingwater.server.api.presenter.ext.replyId
import kr.jadekim.standingwater.server.api.presenter.ext.user
import kr.jadekim.standingwater.service.QuestionService
import kr.jadekim.standingwater.service.ReplyService

fun Route.reply(
    replyService: ReplyService
) {
    get {
        response(
            replyService.getReplies(
                questionId,
                user.id,
                queryParamSafe("order_by")?.let { OrderType.valueOf(it) },
                queryParamSafe("order_desc")?.toBoolean()
            )
        )
    }

    post {
        response(
            replyService.registerReply(
                presentationId,
                questionId,
                user.id,
                bodyParam("content")
            )
        )
    }

    route("/{replyId}") {
        put {
            replyService.modifyReplyContent(presentationId, replyId, bodyParam("content"))
            success()
        }

        delete {
            replyService.deleteReply(presentationId, replyId)
            success()
        }

        route("/like") {
            post {
                replyService.likeReply(presentationId, replyId, user.id)
                success()
            }

            delete {
                replyService.unlikeReply(presentationId, replyId, user.id)
                success()
            }
        }
    }
}