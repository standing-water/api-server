package kr.jadekim.standingwater.server.api.presenter.router

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.routing.*
import kr.jadekim.exception.IllegalParameterException
import kr.jadekim.exception.MissingParameterException
import kr.jadekim.exception.UnauthorizedException
import kr.jadekim.ktor.*
import kr.jadekim.standingwater.server.api.presenter.authentication.UserPrincipal
import kr.jadekim.standingwater.server.api.presenter.ext.presentationId
import kr.jadekim.standingwater.server.api.presenter.ext.user
import kr.jadekim.standingwater.service.FileService
import kr.jadekim.standingwater.service.PresentationService
import java.util.*

fun Route.presentation(
    presentationService: PresentationService,
    fileService: FileService
) {
    get {
        response(presentationService.getAllPresentations())
    }

    post {
        var name: String? = null
        var fileData: ByteArray? = null

        call.receiveMultipart().forEachPart {
            when (it) {
                is PartData.FormItem -> {
                    if (it.name == "name") {
                        name = it.value
                    }
                }
                is PartData.FileItem -> {
                    if (!it.isPdf()) {
                        throw IllegalParameterException("Only support pdf")
                    }

                    fileData = it.streamProvider().readBytes()
                }
            }

            it.dispose()
        }

        if (name.isNullOrBlank() || fileData == null) {
            throw MissingParameterException("Require name, fileId")
        }

        val fileId = fileService.savePresentationFile(fileData!!, ContentType.Application.Pdf.toString())

        response(presentationService.createPresentation(name!!, fileId))
    }

    get("/{enterId}") {
        response(presentationService.getPresentation(pathParam("enterId")))
    }

    route("/{presentationId}") {
        authenticate {
            patch {
                if (!user.isPresenter) {
                    throw UnauthorizedException(context.principal<UserPrincipal>()?.token ?: "")
                }

                presentationService.updateCurrentPresentationPage(
                    presentationId,
                    bodyParam("page").toInt()
                )

                success()
            }
        }

        post("/login") {
            response(presentationService.createSession(presentationId, bodyParamSafe("nickname")))
        }
    }
}

private fun PartData.FileItem.isPdf(): Boolean {
    return originalFileName?.endsWith(".pdf") == true && contentType == ContentType.Application.Pdf
}