package com.github.mattbobambrose.chatroom

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Suppress("ACTUAL_WITHOUT_EXPECT")

actual class ChatService : IChatService {
    override suspend fun postMessage(untimedMessage: UntimedMessage): String {
        val chatMessage = ChatMessage(untimedMessage)
        ServerContext.messages.add(chatMessage)
        return "${chatMessage.untimedMessage.username} said: ${chatMessage.untimedMessage.message}"
    }

    override suspend fun getMessages(lastTimeChecked: Instant): TimedList {
        println("Messages: ${ServerContext.messages.joinToString(" ")}")
        return TimedList(Clock.System.now(), ServerContext.messages.filter {
            it.timestamp > lastTimeChecked
        })
    }
}