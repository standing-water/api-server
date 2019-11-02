package kr.jadekim.ext

import com.fasterxml.jackson.core.type.TypeReference

fun <T> jacksonTypeRef() = object : TypeReference<T>() {}