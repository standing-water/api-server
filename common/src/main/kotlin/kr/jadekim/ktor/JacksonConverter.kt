package kr.jadekim.ktor

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.content.WriterContent
import io.ktor.http.withCharset
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.jvm.javaio.toInputStream
import kotlinx.coroutines.withContext
import kr.jadekim.protocol.ApiResponse

class JacksonConverter(private val objectmapper: ObjectMapper = ObjectMapper()) : ContentConverter {

    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any
    ): Any? {
        val response = if (value !is ApiResponse<*>) {
            if (value is String && value.isBlank()) {
                ApiResponse()
            } else {
                ApiResponse(data = value)
            }
        } else {
            value
        }

        return WriterContent({
            @Suppress("BlockingMethodInNonBlockingContext")
            objectmapper.writeValue(this, response)
        }, ContentType.Application.Json.withCharset(Charsets.UTF_8))
    }

    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val request = context.subject
        var type = request.type
        var isParameter = false
        if (type is Parameters) {
            isParameter = true
            type = HashMap::class
        }

        val value = request.value as? ByteReadChannel ?: return null

        val result = withContext(Dispatchers.IO) {
            value.toInputStream()
                .reader(context.call.request.contentCharset() ?: Charsets.UTF_8)
                .use { objectmapper.readValue(it, type.javaObjectType) }
        }

        if (isParameter) {
            return Parameters.build {
                (result as? HashMap<*, *>)?.forEach {
                    this.append(it.key.toString(), it.value.toString())
                }
            }
        }

        return result
    }
}