package kr.jadekim.ktor

import io.ktor.http.Parameters
import io.ktor.routing.Route
import io.ktor.util.AttributeKey

val RECEIVED_PARAMETERS = AttributeKey<Parameters>("received_parameters")
val ROUTE = AttributeKey<Route>("route")
val PATH = AttributeKey<String>("path")