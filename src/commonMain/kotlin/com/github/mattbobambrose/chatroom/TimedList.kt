package com.github.mattbobambrose.chatroom

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TimedList(
    val lastTimeChecked: Instant,
    val messages: List<ChatMessage>
)