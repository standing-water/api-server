package kr.jadekim.ext

fun Int.dayToHour() = this * 24

fun Int.hourToMinute() = this * 60

fun Int.minuteToSecond() = this * 60

fun Int.secondToMillisecond() = this * 1000

fun Int.toBoolean() = this != 0

fun Long.dayToHour() = this * 24

fun Long.hourToMinute() = this * 60

fun Long.minuteToSecond() = this * 60

fun Long.secondToMillisecond() = this * 1000

fun <T : Comparable<T>> min(a: T, b: T) = if (a < b) a else b

fun <T : Comparable<T>> max(a: T, b: T) = if (a > b) a else b

fun Float.formatScale(scale: Int) = "%.${scale}f".format(this)

fun Double.formatScale(scale: Int) = "%.${scale}f".format(this)