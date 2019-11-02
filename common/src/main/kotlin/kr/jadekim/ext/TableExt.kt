package kr.jadekim.ext

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement

fun <T : Table> T.upsert(
    where: SqlExpressionBuilder.() -> Op<Boolean>,
    onInsert: T.(InsertStatement<Number>) -> Unit,
    onUpdate: T.(UpdateStatement) -> Unit,
    updateWhere: SqlExpressionBuilder.() -> Op<Boolean> = where,
    vararg whereFields: Expression<*>
) {
    val isExist = slice(*whereFields)
        .select(where)
        .empty()

    if (isExist) {
        insert(onInsert)
    } else {
        update(
            where = updateWhere,
            body = onUpdate
        )
    }
}