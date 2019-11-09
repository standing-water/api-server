package kr.jadekim.standingwater.server.api.module

import kotlinx.coroutines.channels.Channel
import kr.jadekim.ext.getString
import kr.jadekim.standingwater.aws.repository.PresentationFileRepositoryImpl
import kr.jadekim.standingwater.domain.Event
import kr.jadekim.standingwater.repository.*
import kr.jadekim.standingwater.server.api.db.repository.PresentationRepositoryImpl
import kr.jadekim.standingwater.server.api.db.repository.QuestionRepositoryImpl
import kr.jadekim.standingwater.server.api.db.repository.ReplyRepositoryImpl
import kr.jadekim.standingwater.server.api.db.repository.UserRepositoryImpl
import kr.jadekim.standingwater.server.api.redis.repository.PresentationCacheRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

val RepositoryModule = module {
    single(named("publish-channel")) { Channel<Pair<Int, Event>>() }
    single(named("subscribe-channel")) { get<Channel<Pair<Int, Event>>>(named("publish-channel")) }
    single {
        PresentationCacheRepositoryImpl(get(named("standingwater-redis"))) as PresentationCacheRepository
    }
    single {
        PresentationFileRepositoryImpl(
            getString("s3.bucket")!!,
            getString("s3.access_key_id")!!,
            getString("s3.secret_access_key")!!
        ) as PresentationFileRepository
    }
    singleBy<PresentationRepository, PresentationRepositoryImpl>()
    singleBy<QuestionRepository, QuestionRepositoryImpl>()
    singleBy<ReplyRepository, ReplyRepositoryImpl>()
    singleBy<UserRepository, UserRepositoryImpl>()
}