package kr.jadekim.ext

fun Boolean.toBinaryInt(): Int {
    return if (this) 1 else 0
}