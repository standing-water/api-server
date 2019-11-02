package kr.jadekim.ext

import com.zaxxer.hikari.HikariDataSource
import kr.jadekim.db.DB
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.onClose
import org.koin.experimental.builder.single
import javax.sql.DataSource

fun Scope.getInt(key: String): Int? {
    return getKoin().getProperty<Int>(key)
}

fun Scope.getInt(key: String, defaultValue: Int): Int {
    return getKoin().getProperty<Int>(key, defaultValue)
}

fun Scope.getBoolean(key: String): Boolean? {
    return getKoin().getProperty<String>(key)?.toBoolean()
}

fun Scope.getBoolean(key: String, defaultValue: Boolean): Boolean {
    return getKoin().getProperty<String>(key)?.toBoolean() ?: defaultValue
}

fun Scope.getString(key: String): String? {
    return getKoin().getProperty(key)
}

fun Scope.getString(key: String, defaultValue: String): String {
    return getKoin().getProperty(key, defaultValue)
}

fun Module.dataSource(
    name: String,
    driver: String,
    url: String,
    username: String,
    password: String,
    isReadOnly: Boolean = false,
    configure: HikariDataSource.() -> Unit = {}
) {
    single(named("$name-datasource")) {
        HikariDataSource().apply {
            this.driverClassName = driver
            this.jdbcUrl = url
            this.username = username
            this.password = password
            this.poolName = name
            this.isReadOnly = isReadOnly
            connectionTimeout = 5.secondToMillisecond().toLong()
            configure()
        }
    }.onClose { it?.close() }
}

fun Module.dataSource(
    name: String,
    isReadOnly: Boolean = false,
    propertyPrefix: String = if (isReadOnly) "db.$name.readonly." else "db.$name.",
    configure: HikariDataSource.() -> Unit = {}
) {
    single(named("$name-datasource")) {
        HikariDataSource().apply {
            driverClassName = getString(propertyPrefix + "driver")
            jdbcUrl = getString(propertyPrefix + "url")
            username = getString(propertyPrefix + "username")
            password = getString(propertyPrefix + "password")
            poolName = name
            this.isReadOnly = isReadOnly
            connectionTimeout = 5.secondToMillisecond().toLong()
            configure()
        }
    }.onClose { it?.close() }
}

fun HikariDataSource.configureMssql() {
    connectionTestQuery = "SELECT 1"
}

fun HikariDataSource.configureMysql() {
    connectionTestQuery = "SELECT 1"
    addDataSourceProperty("useUnicode", "true")
    addDataSourceProperty("characterEncoding", "utf8")
}

fun Module.db(
    name: String,
    createDataSource: Boolean = true,
    withReadOnly: Boolean = false,
    configureDataSource: HikariDataSource.() -> Unit = {}
) {
    if (createDataSource) {
        dataSource(name, configure = configureDataSource)

        if (withReadOnly) {
            dataSource("$name-readonly", isReadOnly = true, configure = configureDataSource)
        } else {
            single(named("$name-readonly-datasource")) {
                dataSource(name)
            }
        }
    }

    single(named("$name-db")) {
        DB(
            dataSource(name),
            dataSource("$name-readonly")
        )
    }
}

fun Scope.dataSource(name: String) = get<DataSource>(named("$name-datasource"))

fun Scope.db(name: String) = get<DB>(named("$name-db"))