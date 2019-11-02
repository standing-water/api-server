package kr.jadekim.exception

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import kr.jadekim.protocol.ApiResponse
import java.util.*

open class ApiException(
    val code: Int,
    val messageKr: String = "기타오류가 발생되었습니다. 고객센터로 문의해주세요.",
    val messageEn: String = "Exceptional errors have occurred. Please contact us.",
    val httpStatus: Int = 500,
    message: String = messageKr,
    cause: Throwable? = null
) : RuntimeException("API-EXCEPTION($code) : $message", cause) {

    open fun toResponse(locale: Locale): ApiResponse<Any> {
        return if (Locale.ENGLISH == locale) {
            ApiResponse(code, messageEn)
        } else {
            ApiResponse(code, messageKr)
        }
    }

    open fun toJson(): ObjectNode {
        val node = JsonNodeFactory.instance.objectNode()

        node.put("code", code)
        node.put("message", message)

        if (cause != null) {
            node.put("cause", cause?.message)
        }

        return node
    }
}

// 1 ~ 99 : 통합 적인 오류
class UnhandledException(cause: Throwable, message: String? = null) : ApiException(
    code = 1,
    message = message ?: cause.message ?: "Unhandled Exception",
    cause = cause
)

class ServerException(message: String, cause: Throwable? = null) : ApiException(
    code = 2,
    message = message,
    cause = cause
)

class CryptoException(
    cause: Throwable
) : ApiException(
    code = 3,
    cause = cause
)

class MissingParameterException(message: String, cause: Throwable? = null) : ApiException(
    code = 4,
    message = message,
    httpStatus = 400,
    cause = cause
)

class IllegalParameterException(message: String, cause: Throwable? = null) : ApiException(
    code = 5,
    message = message,
    httpStatus = 400,
    cause = cause
)

class MaintenanceException : ApiException(
    code = 6,
    messageKr = "현재 점검중입니다. 잠시 후 다시 시도해주세요.",
    messageEn = "현재 점검중입니다. 잠시 후 다시 시도해주세요.",
    httpStatus = 400
)

class IllegalValueException(
    message: String,
    cause: Throwable? = null
) : ApiException(
    code = 7,
    message = message,
    httpStatus = 400,
    cause = cause
)

class UnauthorizedException(
    val token: String,
    cause: Throwable? = null
) : ApiException(
    code = 8,
    messageKr = "인증에 실패하였습니다. 고객센터로 문의해주세요.",
    messageEn = "인증에 실패하였습니다. 고객센터로 문의해주세요.",
    httpStatus = 401,
    cause = cause
) {
    override fun toJson(): ObjectNode {
        return super.toJson().apply {
            put("token", token)
        }
    }
}

class QueryEmptyResultException(
    cause: Throwable? = null
) : ApiException(
    code = 9,
    cause = cause,
    message = cause?.message ?: ""
)

class InvalidRequestException(
    cause: Throwable? = null
) : ApiException(
    code = 10,
    cause = cause
)