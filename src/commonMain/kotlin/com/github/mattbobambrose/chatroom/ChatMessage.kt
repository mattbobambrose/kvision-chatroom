package com.github.mattbobambrose.chatroom

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val message: String,
    val user: String,
) {
    val timestamp: Instant = Clock.System.now()

    fun displayMessage(): String {
        return "$user said: $message"
    }
}