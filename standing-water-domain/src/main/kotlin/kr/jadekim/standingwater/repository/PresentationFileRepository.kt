package kr.jadekim.standingwater.repository

import java.util.*

interface PresentationFileRepository {

    suspend fun saveFile(
        fileData: ByteArray,
        mimeType: String
    ): UUID
}