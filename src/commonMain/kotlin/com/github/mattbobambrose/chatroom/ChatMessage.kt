package com.github.mattbobambrose.chatroom

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val username: String,
    val room: String,
    val message: String,
) {
    fun displayMessage() = "$username said: $message from $room"
}