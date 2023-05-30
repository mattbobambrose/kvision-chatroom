package com.github.mattbobambrose.chatroom

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Suppress("ACTUAL_WITHOUT_EXPECT")

actual class ChatService : IChatService {
    override suspend fun postMessage(message: String, username: String): String {
        println(message)
        val chatMessage = ChatMessage(message, username)
        ServerContext.messages.add(chatMessage)
        return "$username said: $message"
    }

    override suspend fun getMessages(lastTimeChecked: Instant): TimedList {
        println("Messages: ${ServerContext.messages.joinToString(" ")}")
        return TimedList(Clock.System.now(), ServerContext.messages.filter {
            it.timestamp > lastTimeChecked
        })
    }
}