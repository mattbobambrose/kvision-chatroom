package com.github.mattbobambrose.chatroom

import io.kvision.annotations.KVService
import kotlinx.datetime.Instant

@KVService
interface IChatService {
    suspend fun postMessage(message: String, username: String): String
    suspend fun getMessages(lastTimeChecked: Instant): TimedList
}