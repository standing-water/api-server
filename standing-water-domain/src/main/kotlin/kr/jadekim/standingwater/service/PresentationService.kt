package kr.jadekim.standingwater.service

import kr.jadekim.exception.IllegalParameterException
import kr.jadekim.standingwater.domain.Event
import kr.jadekim.standingwater.domain.Presentation
import kr.jadekim.standingwater.domain.User
import kr.jadekim.standingwater.domain.alias.PublishChannel
import kr.jadekim.standingwater.repository.PresentationCacheRepository
import kr.jadekim.standingwater.repository.PresentationRepository
import kr.jadekim.standingwater.repository.UserRepository
import kr.jadekim.standingwater.service.protocol.*
import java.util.*

class PresentationService(
    private val realtimeService: RealtimeService,
    private val presentationRepository: PresentationRepository,
    private val presentationCacheRepository: PresentationCacheRepository,
    private val userRepository: UserRepository,
    private val publishChannel: PublishChannel,
    private val fileServerBaseUrl: String
) {

    suspend fun getAllPresentations() = presentationRepository.getAll()
        .map {
            PresentationListItem(
                it.id,
                it.enterId,
                "$fileServerBaseUrl/file/${it.fileId}",
                it.name
            )
        }
        .let { ListResponse(it) }

    suspend fun createPresentation(
        name: String,
        fileId: UUID
    ): CreatePresentationResponse {
        val enterId = Presentation.createEnterId()
        val id = presentationRepository.add(enterId, fileId, name)
        val authToken = UUID.randomUUID()

        userRepository.add(id, "발표자", authToken, true)

        return CreatePresentationResponse(id, enterId, "$fileServerBaseUrl/file/$fileId", authToken.toString())
    }

    suspend fun getPresentation(enterId: String): PresentationResponse {
        val presentation = presentationRepository.get(enterId) ?: throw IllegalParameterException("Not found enterId")

        return PresentationResponse(
            presentation.id,
            presentation.enterId,
            "$fileServerBaseUrl/file/${presentation.fileId}",
            presentation.name,
            presentationCacheRepository.getCurrentPage(presentation.id) ?: 1,
            realtimeService.getActiveCount(presentation.id)
        )
    }

    suspend fun updateCurrentPresentationPage(presentationId: Int, page: Int) {
        presentationCacheRepository.setCurrentPage(presentationId, page)

        publishChannel.send(presentationId to Event.changePage(page))
    }

    suspend fun createSession(presentationId: Int, nickname: String?): UserResponse {
        val authToken = UUID.randomUUID()
        val realNickname = nickname ?: User.createRandomNickname()

        userRepository.add(presentationId, realNickname, authToken)

        return UserResponse(realNickname, authToken.toString())
    }
}