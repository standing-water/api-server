package kr.jadekim.standingwater.server.api.base.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.jadekim.jackson.TimestampDateFormat
import kr.jadekim.jackson.timestampJodaTimeModule

object Jackson {

    val mapper = jacksonObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setDateFormat(TimestampDateFormat())
        .registerModule(timestampJodaTimeModule)
}