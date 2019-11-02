package kr.jadekim.standingwater.server.api.module

import kr.jadekim.ext.configureMysql
import kr.jadekim.ext.db
import org.koin.dsl.module

val DBModule = module {
    db("standingwater") {
        configureMysql()
    }
}