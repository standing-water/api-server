package kr.jadekim.standingwater.server.api.module

import kr.jadekim.ext.getString
import kr.jadekim.standingwater.service.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.single

val ServiceModule = module {
    single<UserService>()
    single<FileService>()
    single { QuestionService(get(), get(), get(), get(named("publish-channel"))) }
    single { ReplyService(get(), get(), get(named("publish-channel"))) }
    single {
        PresentationService(
            get(),
            get(),
            get(),
            get(),
            get(named("publish-channel")),
            getString("s3.base_url")!!
        )
    }
    single { ChatService(get(named("publish-channel"))) }
    single { RealtimeService(get(), get(named("publish-channel")), get(named("subscribe-channel"))) }
}