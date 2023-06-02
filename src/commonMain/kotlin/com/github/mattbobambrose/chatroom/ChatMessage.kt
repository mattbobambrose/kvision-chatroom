package com.github.mattbobambrose.chatroom

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val untimedMessage: UntimedMessage
) {
    val timestamp: Instant = Clock.System.now()

    fun displayMessage(): String {
        return "${untimedMessage.username} said: ${untimedMessage.message}"
    }
}