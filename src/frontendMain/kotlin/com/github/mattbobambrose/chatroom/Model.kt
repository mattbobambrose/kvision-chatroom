package com.github.mattbobambrose.chatroom

import io.kvision.remote.getService
import kotlinx.datetime.Instant

object Model {

    private val chatService = getService<IChatService>()

    suspend fun postMessage(username: String, room: String, message: String): String {
        return chatService.postMessage(ChatMessage(username, room, message))
    }

    suspend fun getMessages(lastTimeChecked: Instant): TimedList {
        return chatService.getMessages(lastTimeChecked)
    }

    suspend fun changeRoom(room: String): String {
        return chatService.changeRoom(room)
    }
}