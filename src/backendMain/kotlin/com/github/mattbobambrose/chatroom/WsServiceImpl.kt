package com.github.mattbobambrose.chatroom

import com.github.pambrose.kvision_websockets.BrowserSession
import com.google.inject.Inject
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class WsService : IWsService {
    @Inject
    lateinit var wsSession: WebSocketServerSession

    var currentRoomRef = -1
    val outputChannel = Channel<ChatMessage>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun openBidirChannel(input: ReceiveChannel<ChatMessage>, output: SendChannel<ChatMessage>) {
        val browserSession = wsSession.call.sessions.get<BrowserSession>() ?: error("No BrowserSession found")
        val instanceName = browserSession.id
        println("WsService created: $instanceName")
        instanceMap[instanceName] = this

        output.invokeOnClose {
            instanceMap.remove(instanceName)
            println("output closed: $instanceName")
        }

        runBlocking {
            // Continuously send all messages to client
            launch {
                for (msg in outputChannel) {
                    output.send(msg)
                }
            }

            launch {
//                 Return previous messages
//                refreshMessages()

                // Saves and distributes messages
                for (msg in input) {
                    // Insert into database
                    transaction {
                        MessagesTable.insert {
                            it[username] = msg.username
                            it[roomRef] = msg.roomId
                            it[message] = msg.message
                        }
                    }

                    // Send to all clients
                    instanceMap.values.forEach {
                        it.outputChannel.send(msg)
                    }
                }
            }
        }
    }

    suspend fun refreshMessages() {
        transaction {
            MessagesTable.select { MessagesTable.roomRef eq currentRoomRef }
                .map { msg ->
                    val username = msg[MessagesTable.username]
                    val roomId = msg[MessagesTable.roomRef]
                    val message = msg[MessagesTable.message]
                    ChatMessage(username, roomId, message)
                }
        }.forEach {
            outputChannel.send(it)
        }
    }

    companion object {
        val instanceMap = mutableMapOf<String, WsService>()
    }
}