package kr.jadekim.standingwater.domain.alias

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kr.jadekim.standingwater.domain.Event

typealias SubscribeChannel = ReceiveChannel<Pair<Int, Event>>
typealias PublishChannel = SendChannel<Pair<Int, Event>>