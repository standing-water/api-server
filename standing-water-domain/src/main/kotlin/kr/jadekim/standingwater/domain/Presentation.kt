package kr.jadekim.standingwater.domain

import java.nio.ByteBuffer
import java.util.UUID

data class Presentation(
    val id: Int,
    val enterId: String,
    val fileId: UUID,
    val name: String
) {

    companion object {

        fun createEnterId(): String {
            val uuid = UUID.randomUUID()
            val value = ByteBuffer.wrap(uuid.toString().toByteArray()).long

            return value.toString(Character.MAX_RADIX)
        }
    }
}