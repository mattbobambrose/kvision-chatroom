package com.github.mattbobambrose.chatroom

import io.kvision.remote.getService

object Model {

    private val chatService = getService<IChatService>()

    suspend fun changeRoom(roomId: Int) {
        chatService.changeRoom(roomId)
    }

    suspend fun getRoomNames(): List<RoomIdentifier> {
        return chatService.getRoomNames()
    }
}