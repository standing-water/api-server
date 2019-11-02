package kr.jadekim.db.dbms

import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.vendors.DatabaseDialect

internal val currentDialect: DatabaseDialect get() = TransactionManager.current().db.dialect

