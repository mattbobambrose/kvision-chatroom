package com.github.mattbobambrose.chatroom

import io.kvision.remote.getService
import kotlinx.datetime.Instant

object Model {

    private val chatService = getService<IChatService>()

    suspend fun postMessage(message: String, username: String): String {
        return chatService.postMessage(UntimedMessage(message, username))
    }

    suspend fun getMessages(lastTimeChecked: Instant): TimedList {
        return chatService.getMessages(lastTimeChecked)
    }
}