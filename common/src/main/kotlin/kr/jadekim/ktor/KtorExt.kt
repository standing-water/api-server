package kr.jadekim.ktor

import io.ktor.application.ApplicationCall
import io.ktor.http.Parameters
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.toMap
import kr.jadekim.exception.MissingParameterException
import kr.jadekim.protocol.ApiResponse

inline val PipelineContext<*, ApplicationCall>.pathParam: Parameters get() = context.parameters

inline val PipelineContext<*, ApplicationCall>.queryParam: Parameters get() = context.request.queryParameters

suspend fun PipelineContext<*, ApplicationCall>.bodyParam(): Parameters? {
    var cache = context.attributes.getOrNull(RECEIVED_PARAMETERS)

    if (cache == null) {
        context.receiveOrNull<Parameters>()?.let {
            context.attributes.put(RECEIVED_PARAMETERS, it)
            cache = it
        }
    }

    return cache
}

fun PipelineContext<*, ApplicationCall>.pathParamSafe(key: String, default: String? = null): String? {
    return pathParam[key] ?: default
}

fun PipelineContext<*, ApplicationCall>.pathParam(key: String, default: String? = null): String {
    return pathParamSafe(key, default) ?: throw MissingParameterException("required $key")
}

fun PipelineContext<*, ApplicationCall>.queryParamSafe(key: String, default: String? = null): String? {
    return queryParam[key] ?: default
}

fun PipelineContext<*, ApplicationCall>.queryParam(key: String, default: String? = null): String {
    return queryParamSafe(key, default) ?: throw MissingParameterException("required $key")
}

suspend fun PipelineContext<*, ApplicationCall>.bodyParamListSafe(key: String): List<String> {
    return bodyParam()?.getAll(key) ?: emptyList()
}

suspend fun PipelineContext<*, ApplicationCall>.bodyParamList(key: String): List<String> {
    val result = bodyParamListSafe(key)

    if (result.isEmpty()) {
        throw MissingParameterException("required $key")
    }

    return result
}

suspend fun PipelineContext<*, ApplicationCall>.bodyParamSafe(key: String, default: String? = null): String? {
    return bodyParam()?.get(key) ?: default
}

suspend fun PipelineContext<*, ApplicationCall>.bodyParam(key: String, default: String? = null): String {
    return bodyParamSafe(key, default) ?: throw MissingParameterException("required $key")
}

suspend fun PipelineContext<*, ApplicationCall>.response(value: Any?) = context.respond(value ?: "")

suspend fun PipelineContext<*, ApplicationCall>.success(data: Any? = null, message: String = "success") {
    return response(ApiResponse(message = message, data = data))
}

fun Parameters?.toSingleValueMap(): Map<String, String> {
    return this?.toMap()
        ?.mapValues { it.value.firstOrNull() }
        ?.filterValues { !it.isNullOrBlank() }
        ?.mapValues { it.value!! }
        ?: emptyMap()
}