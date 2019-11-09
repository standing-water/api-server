package kr.jadekim.standingwater.server.api.presenter.router

import io.ktor.routing.*
import kr.jadekim.ktor.bodyParam
import kr.jadekim.ktor.queryParamSafe
import kr.jadekim.ktor.response
import kr.jadekim.ktor.success
import kr.jadekim.standingwater.enumuration.OrderType
import kr.jadekim.standingwater.server.api.presenter.ext.presentationId
import kr.jadekim.standingwater.server.api.presenter.ext.questionId
import kr.jadekim.standingwater.server.api.presenter.ext.user
import kr.jadekim.standingwater.service.QuestionService

fun Route.question(
    questionService: QuestionService
) {
    get {
        response(
            questionService.getQuestions(
                presentationId,
                user.id,
                queryParamSafe("page")?.toIntOrNull(),
                queryParamSafe("order_by")?.let { OrderType.valueOf(it) },
                queryParamSafe("order_desc")?.toBoolean()
            )
        )
    }

    post {
        response(
            questionService.registerQuestion(
                presentationId,
                user.id,
                bodyParam("page").toInt(),
                bodyParam("content")
            )
        )
    }

    route("/{questionId}") {
        put {
            questionService.modifyQuestionContent(presentationId, questionId, bodyParam("content"))
            success()
        }

        delete {
            questionService.deleteQuestion(presentationId, questionId)
            success()
        }

        route("/like") {
            post {
                questionService.likeQuestion(presentationId, questionId, user.id)
                success()
            }

            delete {
                questionService.unlikeQuestion(presentationId, questionId, user.id)
                success()
            }
        }
    }
}