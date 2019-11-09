package kr.jadekim.standingwater.server.api.presenter.ext

import io.ktor.application.ApplicationCall
import io.ktor.auth.principal
import io.ktor.util.pipeline.PipelineContext
import kr.jadekim.exception.UnauthorizedException
import kr.jadekim.ktor.pathParam
import kr.jadekim.standingwater.server.api.presenter.authentication.UserPrincipal
import kr.jadekim.standingwater.server.api.presenter.authentication.bearerAuthenticationCredentials

internal val PipelineContext<Unit, ApplicationCall>.user
    get() = context.principal<UserPrincipal>()?.user
        ?: throw UnauthorizedException(context.request.bearerAuthenticationCredentials()?.token ?: "")

internal val PipelineContext<Unit, ApplicationCall>.presentationId
    get() = pathParam("presentationId").toInt()

internal val PipelineContext<Unit, ApplicationCall>.questionId
get() = pathParam("questionId").toInt()

internal val PipelineContext<Unit, ApplicationCall>.replyId
get() = pathParam("replyId").toInt()