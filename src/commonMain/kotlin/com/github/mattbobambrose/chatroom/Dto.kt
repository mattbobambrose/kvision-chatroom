package com.github.mattbobambrose.chatroom

import kotlinx.serialization.Serializable

@Serializable
class BrowserSessionResponse(
    val id: String,
    val msg: String,
)

@Serializable
class RoomIdentifier(
    val id: Int,
    val room: String,
)