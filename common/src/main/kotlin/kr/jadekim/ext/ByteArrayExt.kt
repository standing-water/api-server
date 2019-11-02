package kr.jadekim.ext

fun ByteArray.toHex(): String {
    val sb = StringBuilder()

    for (b in this) {
        sb.append(String.format("%02X", b))
    }

    return sb.toString()
}