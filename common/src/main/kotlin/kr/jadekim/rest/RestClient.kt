package kr.jadekim.rest

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.call
import io.ktor.client.call.receive
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.Charsets
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLProtocol
import kr.jadekim.ext.secondToMillisecond
import java.util.*

class RestClient(
    val host: String,
    val isHttps: Boolean = false,
    configure: (HttpClientConfig<*>.() -> Unit)? = null
) {

    var basePath = ""

    private val defaultConfigure: HttpClientConfig<*>.() -> Unit = {
        install(JsonFeature) {
            serializer = JacksonSerializer() {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }

    private val client = HttpClient(Apache) {
        engine {
            followRedirects = true
            socketTimeout = 5.secondToMillisecond()
            connectTimeout = 5.secondToMillisecond()
            connectionRequestTimeout = 20.secondToMillisecond()
        }

        Charsets {
            register(Charsets.UTF_8)
            sendCharset = Charsets.UTF_8
            responseCharsetFallback = Charsets.UTF_8
        }

        defaultRequest {
            url {
                host = this@RestClient.host

                if (isHttps) {
                    protocol = URLProtocol.HTTPS
                }
            }
        }

        if (configure == null) {
            defaultConfigure()
        } else {
            configure()
        }
    }

    suspend fun call(method: HttpMethod, path: String, parameters: Map<String, Any> = emptyMap()) = client.call {
        url {
            encodedPath = basePath + path

            if (method == HttpMethod.Get) {
                this.parameters.appendAll(parameters)
            }
        }

        this.method = method

        this.header("requestId", UUID.randomUUID().toString())

        if (method != HttpMethod.Get) {
            this.body = FormDataContent(Parameters.build { appendAll(parameters) })
        }
    }

    suspend inline fun <reified T> request(
        method: HttpMethod,
        path: String,
        parameters: Map<String, Any> = emptyMap()
    ): T {
        return call(method, path, parameters).receive()
    }

    suspend inline fun <reified T> get(path: String, parameters: Map<String, Any> = emptyMap()): T {
        return request(HttpMethod.Get, path, parameters)
    }

    suspend inline fun <reified T> post(path: String, parameters: Map<String, Any> = emptyMap()): T {
        return request(HttpMethod.Post, path, parameters)
    }

    suspend inline fun <reified T> put(path: String, parameters: Map<String, Any> = emptyMap()): T {
        return request(HttpMethod.Put, path, parameters)
    }

    suspend inline fun <reified T> delete(path: String): T {
        return request(HttpMethod.Delete, path)
    }

    private fun ParametersBuilder.appendAll(data: Map<String, Any>) {
        data.forEach { k, v ->
            if (v is Iterable<*>) {
                appendAll(k, v.map { it.toString() })
            } else {
                append(k, v.toString())
            }
        }
    }
}