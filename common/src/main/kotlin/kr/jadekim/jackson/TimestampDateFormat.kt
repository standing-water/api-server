package kr.jadekim.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.text.DateFormat
import java.text.FieldPosition
import java.text.ParsePosition
import java.util.*

class TimestampDateFormat : DateFormat() {

    override fun parse(source: String?, pos: ParsePosition?): Date? {
        return source?.toLongOrNull()?.let { Date(it) }
    }

    override fun format(date: Date?, toAppendTo: StringBuffer?, fieldPosition: FieldPosition?): StringBuffer? {
        if (toAppendTo == null || date == null) {
            return null
        }

        return toAppendTo.append(date.time)
    }
}

class DateTimeSerializer : JsonSerializer<DateTime>() {

    override fun serialize(value: DateTime?, gen: JsonGenerator, serializers: SerializerProvider) {
        value?.toDate()?.time?.let {
            gen.writeNumber(it)
        }
    }
}

class DateTimeDeserializer : JsonDeserializer<DateTime>() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DateTime? {
        return try {
            DateTime(p.longValue)
        } catch (e: Exception) {
            logger.warn("Fail to deserialize DateTime")
            null
        }
    }
}

val timestampJodaTimeModule = SimpleModule()
    .addSerializer(DateTime::class.java, DateTimeSerializer())
    .addDeserializer(DateTime::class.java, DateTimeDeserializer())!!