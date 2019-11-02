package kr.jadekim.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import javax.sql.DataSource
import kr.jadekim.exception.QueryEmptyResultException

class DB(
    dataSource: DataSource,
    readOnlyDataSource: DataSource,
    name: String? = null
) {

    private val database = Database.connect(dataSource)
    private val readOnlyDatabase = Database.connect(readOnlyDataSource)

    suspend fun <T> execute(statement: suspend Transaction.() -> T): T {
        return newSuspendedTransaction(Dispatchers.IO, database, statement)
    }

    suspend fun <T> read(statement: suspend Transaction.() -> T): T {
        return try {
            newSuspendedTransaction(Dispatchers.IO, readOnlyDatabase, statement)
        } catch (e: EntityNotFoundException) {
            throw QueryEmptyResultException(e)
        } catch (e: NoSuchElementException) {
            throw QueryEmptyResultException(e)
        }
    }
}