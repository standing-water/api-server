package kr.jadekim.standingwater.service

import kr.jadekim.standingwater.repository.PresentationFileRepository

class FileService(
    private val presentationFileRepository: PresentationFileRepository
) {

    suspend fun savePresentationFile(
        fileData: ByteArray,
        mimeType: String
    ) = presentationFileRepository.saveFile(fileData, mimeType)
}