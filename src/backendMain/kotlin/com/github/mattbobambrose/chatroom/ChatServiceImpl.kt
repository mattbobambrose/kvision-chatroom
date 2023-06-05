package com.github.mattbobambrose.chatroom

import com.github.pambrose.kvision_websockets.BrowserSession
import com.google.inject.Inject
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Suppress("ACTUAL_WITHOUT_EXPECT")

actual class ChatService : IChatService {
    @Inject
    lateinit var call: ApplicationCall

    override suspend fun postMessage(chatMessage: ChatMessage): String {
        /*
                val chatMessage = ChatMessage(chatMessage)
                return "${chatMessage.untimedMessage.username} said: ${chatMessage.untimedMessage.message}"
        */
        return ""
    }

    override suspend fun getMessages(lastTimeChecked: Instant): TimedList {
        /*
                println("Messages: ${ServerContext.messages.joinToString(" ")}")
                return TimedList(Clock.System.now(), ServerContext.messages.filter {
                    it.timestamp > lastTimeChecked
                })
        */
        return TimedList(Clock.System.now(), emptyList())
    }

    override suspend fun changeRoom(room: String): String {
        val browserSession = call.sessions.get<BrowserSession>() ?: error("No BrowserSession found")
        val id = browserSession.id
        val wsService = WsService.instanceMap[id] ?: error("No WsService found for $id")
        wsService.roomName = room
        wsService.refreshMessages()
        return "Room changed to $room"
    }
}