package com.github.mattbobambrose.chatroom

import kotlinx.serialization.Serializable

@Serializable
class UntimedMessage(
    val message: String,
    val username: String,
)