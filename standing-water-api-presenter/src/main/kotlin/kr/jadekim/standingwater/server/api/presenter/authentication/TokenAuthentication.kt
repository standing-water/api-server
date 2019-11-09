package kr.jadekim.standingwater.server.api.presenter.authentication

import io.ktor.auth.Principal
import kr.jadekim.standingwater.domain.User

data class UserPrincipal(val token: String, val user: User) : Principal