package kr.jadekim.ktor

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.features.XForwardedHeaderSupport
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.acceptLanguage
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import kr.jadekim.enumuration.ServiceEnvironment
import kr.jadekim.exception.ApiException
import kr.jadekim.exception.UnhandledException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*

fun Application.baseModule(
    serviceEnv: ServiceEnvironment,
    version: String,
    filterParameters: List<String> = emptyList(),
    jackson: ObjectMapper = ObjectMapper(),
    extraLoggingValues: PipelineContext<Unit, ApplicationCall>.() -> Map<String, String> = { emptyMap() }
) {

    val errorLogger = LoggerFactory.getLogger("ErrorLogger")

    install(XForwardedHeaderSupport)

    install(PathNormalizeFeature)

    install(Slf4jCallLogging) {
        this.serviceEnv = serviceEnv
        this.release = version
        this.filterParameters = filterParameters
        this.mapper = jackson
        this.mdcProvider = {
            extraLoggingValues().forEach { k, v -> MDC.put(k, v) }
        }
    }

    install(AutoHeadResponse)

    install(ContentNegotiation) {
        register(
            ContentType.Application.Json,
            JacksonConverter(jackson)
        )
    }

    install(StatusPages) {
        status(HttpStatusCode.InternalServerError) {
            val acceptLanguage = this.context.request.acceptLanguage()
            val locale = if (acceptLanguage == null || acceptLanguage.contains("KR", ignoreCase = true)) {
                Locale.KOREA
            } else {
                Locale.ENGLISH
            }

            call.respond(HttpStatusCode.InternalServerError, UnhandledException(Exception()).toResponse(locale))
        }
        exception<Throwable> {
            throw UnhandledException(it, it.message)
        }
        exception<ApiException> {
            val acceptLanguage = this.context.request.acceptLanguage()
            val locale = if (acceptLanguage == null || acceptLanguage.contains("KR", ignoreCase = true)) {
                Locale.KOREA
            } else {
                Locale.ENGLISH
            }

            if (it.code < 3) {
                errorLogger.error(it.message, it)
            }
            if (it.code != 6 && (it.code < 100 || it.code >= 1000)) {
                errorLogger.warn(it.message, it)
            } else {
                errorLogger.info(it.message, it)
            }

            call.respond(HttpStatusCode.fromValue(it.httpStatus), it.toResponse(locale))
        }
    }
}