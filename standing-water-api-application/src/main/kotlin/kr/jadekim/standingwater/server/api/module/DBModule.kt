package kr.jadekim.standingwater.server.api.module

import kr.jadekim.ext.configureMysql
import kr.jadekim.ext.db
import kr.jadekim.standingwater.server.api.db.dao.PresentationDao
import kr.jadekim.standingwater.server.api.db.dao.QuestionDao
import kr.jadekim.standingwater.server.api.db.dao.ReplyDao
import kr.jadekim.standingwater.server.api.db.dao.UserDao
import org.koin.dsl.module

val DBModule = module {
    db("standingwater") {
        configureMysql()
    }

    single { PresentationDao(db("standingwater")) }
    single { QuestionDao(db("standingwater")) }
    single { ReplyDao(db("standingwater")) }
    single { UserDao(db("standingwater")) }
}