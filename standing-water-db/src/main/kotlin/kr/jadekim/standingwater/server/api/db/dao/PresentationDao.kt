package kr.jadekim.standingwater.server.api.db.dao

import kr.jadekim.db.DB
import kr.jadekim.standingwater.domain.Presentation
import kr.jadekim.standingwater.server.api.db.table.Presentations
import kr.jadekim.standingwater.server.api.db.table.Users
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.util.*

class PresentationDao(
    private val db: DB
) {

    suspend fun getAll() = db.read {
        Presentations.slice(
            Presentations.id,
            Presentations.enterId,
            Presentations.fileId,
            Presentations.name
        )
            .selectAll()
            .map { it.toDomainModel() }
    }

    suspend fun get(enterId: String) = db.read {
        Presentations.slice(
            Presentations.id,
            Presentations.enterId,
            Presentations.fileId,
            Presentations.name
        )
            .select { Presentations.enterId eq enterId }
            .firstOrNull()?.toDomainModel()
    }

    suspend fun add(enterId: String, fileId: UUID, name: String) = db.execute {
        Presentations.insertAndGetId {
            it[this.enterId] = enterId
            it[this.fileId] = fileId
            it[this.name] = name
        }.value
    }

    private fun ResultRow.toDomainModel() = Presentation(
        get(Presentations.id).value,
        get(Presentations.enterId),
        get(Presentations.fileId),
        get(Presentations.name)
    )
}