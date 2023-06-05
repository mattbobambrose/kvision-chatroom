package com.github.mattbobambrose.chatroom

import io.kvision.annotations.KVService
import kotlinx.datetime.Instant

@KVService
interface IChatService {
    suspend fun postMessage(chatMessage: ChatMessage): String
    suspend fun getMessages(lastTimeChecked: Instant): TimedList
    suspend fun changeRoom(room: String): String
}