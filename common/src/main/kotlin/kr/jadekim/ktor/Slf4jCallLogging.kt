package kr.jadekim.ktor

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.*
import io.ktor.features.UnsupportedMediaTypeException
import io.ktor.http.HttpMethod
import io.ktor.request.*
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.toMap
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import kr.jadekim.enumuration.ServiceEnvironment
import org.slf4j.LoggerFactory
import org.slf4j.MDC

class Slf4jCallLogging private constructor(
    private val serviceEnv: ServiceEnvironment?,
    private val release: String,
    private val filterParameters: List<String> = emptyList(),
    private val mapper: ObjectMapper = ObjectMapper(),
    private val mdcProvider: PipelineContext<Unit, ApplicationCall>.() -> Unit = {}
) {

    class Configuration {
        var serviceEnv: ServiceEnvironment? = null
        var release: String = "not_set"
        var filterParameters: List<String> = emptyList()
        var mapper: ObjectMapper = ObjectMapper()
        var mdcProvider: PipelineContext<Unit, ApplicationCall>.() -> Unit = {}
    }

    companion object Feature : ApplicationFeature<Application, Configuration, Slf4jCallLogging> {

        private val logger = LoggerFactory.getLogger("RequestLogger")

        override val key: AttributeKey<Slf4jCallLogging> = AttributeKey("Slf4jCallLoggingFeature")

        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit
        ): Slf4jCallLogging {
            val configuration = Configuration().apply(configure)
            val feature = Slf4jCallLogging(
                configuration.serviceEnv,
                configuration.release,
                configuration.filterParameters,
                configuration.mapper,
                configuration.mdcProvider
            )

            pipeline.intercept(ApplicationCallPipeline.Call) {
                val method = call.request.httpMethod
                val request = call.attributes.getOrNull(PATH)
                    ?: "${context.request.path()}(method:${method.value})"

                val preHandleTime = System.currentTimeMillis()
                MDC.put("preHandleTime", preHandleTime.toString())
                MDC.put("serviceEnv", feature.serviceEnv?.name ?: "not_set")
                MDC.put("deployVersion", feature.release)
                MDC.put("request", request)
                MDC.put("remoteAddress", call.request.host())
                MDC.put("userAgent", call.request.userAgent())
                call.request.headers.forEach { key, values ->
                    values.forEachIndexed { index, value ->
                        MDC.put("headers.$key.$index", value)
                    }
                }

                var parameters = pathParam.toMap() + queryParam.toMap()

                if ((method == HttpMethod.Post || method == HttpMethod.Put || method == HttpMethod.Patch)
                    && !context.request.isMultipart()
                ) {
                    try {
                        withContext(MDCContext()) {
                            bodyParam()?.toMap()?.let {
                                parameters = parameters + it
                            }
                        }
                    } catch (e: UnsupportedMediaTypeException) {
                        //do nothing
                    }
                }

                parameters.filter { it.key !in feature.filterParameters }
                    .forEach { key, values ->
                        values.forEachIndexed { index, value ->
                            MDC.put("parameters.$key.$index", value)
                        }
                    }

                feature.mdcProvider(this@intercept)

                withContext(MDCContext()) {
                    proceed()

                    val status = call.response.status()?.value?.toString()

                    MDC.put("durationToHandle", "${System.currentTimeMillis() - preHandleTime}")
                    MDC.put("status", status)

                    logger.info("$request - $status")

                    MDC.clear()
                }
            }

            return feature
        }
    }
}