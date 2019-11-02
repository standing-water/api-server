package kr.jadekim.util

import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.concurrent.thread

fun loadProperties(vararg propertyFiles: File, properties: Properties = Properties()): Properties {
    properties.putAll(System.getProperties())
    properties.putAll(System.getenv())

    propertyFiles
        .filter { it.canRead() }
        .map { it.inputStream() }
        .forEach {
            it.use {
                properties.load(it)
            }
        }

    return properties
}

fun loadProperties(vararg propertyStreams: InputStream, properties: Properties = Properties()): Properties {
    properties.putAll(System.getProperties())
    properties.putAll(System.getenv())

    propertyStreams
        .forEach { stream ->
            stream.use {
                properties.load(it)
            }
        }

    return properties
}

fun shutdownHook(block: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(thread(start = false, block = block))
}