package com.github.mattbobambrose.chatroom

import com.github.mattbobambrose.chatroom.ServerContext.messages
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class WsService : IWsService {
    val instanceName = "WsService instance ${Random.nextLong()})}"
    val outputChannel = Channel<ChatMessage>()

    init {
        println("WsService created: $instanceName")
        instanceMap[instanceName] = this
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun openBidirChannel(input: ReceiveChannel<UntimedMessage>, output: SendChannel<ChatMessage>) {
        output.invokeOnClose {
            instanceMap.remove(instanceName)
            println("output closed: $instanceName")
        }

        runBlocking {
            launch {
                for (msg in messages) {
                    output.send(msg)
                }
            }
        }

        runBlocking {
            launch {
                for (msg in input) {
                    val chatMessage = ChatMessage(msg)
                    messages.add(chatMessage)
                    instanceMap.values.forEach {
                        it.outputChannel.send(chatMessage)
                    }
                }
            }

            launch {
                for (msg in outputChannel) {
                    output.send(msg)
                }
            }
        }
    }

    companion object {
        val instanceMap = mutableMapOf<String, WsService>()

    }
}