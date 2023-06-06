package com.github.mattbobambrose.chatroom

import com.github.pambrose.kvision_websockets.BrowserSession
import com.google.inject.Inject
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("ACTUAL_WITHOUT_EXPECT")

actual class ChatService : IChatService {
    @Inject
    lateinit var call: ApplicationCall

    override suspend fun changeRoom(roomId: Int): String {
        val browserSession = call.sessions.get<BrowserSession>() ?: error("No BrowserSession found")
        val id = browserSession.id
        println("Browser session id: $id")
        val wsService = WsService.instanceMap[id] ?: error("No WsService found for $id")
        wsService.currentRoomRef = roomId
        wsService.refreshMessages()
        return "Room changed to $roomId"
    }

    override suspend fun getRoomNames() =
        transaction {
            RoomsTable.selectAll().map {
                RoomIdentifier(
                    it[RoomsTable.id].value,
                    it[RoomsTable.name]
                )
            }
        }
}