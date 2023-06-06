package com.github.mattbobambrose.chatroom

import io.kvision.annotations.KVService

@KVService
interface IChatService {
    suspend fun changeRoom(roomId: Int): String
    suspend fun getRoomNames(): List<RoomIdentifier>
}