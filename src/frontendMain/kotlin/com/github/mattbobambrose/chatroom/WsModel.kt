package com.github.mattbobambrose.chatroom

import io.kvision.remote.getService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object WsModel {

    private val service = getService<IWsService>()

    fun connectToWebSocket(sendChannel: Channel<ChatMessage>, receiveChannel: Channel<ChatMessage>) {
        AppScope.launch {
            service.openBidirChannel { output: SendChannel<ChatMessage>, input: ReceiveChannel<ChatMessage> ->
                coroutineScope {
                    launch {
                        while (true) {
                            val msg = sendChannel.receive()
                            output.send(msg)
                        }
                    }

                    launch {
                        for (msg in input) {
                            receiveChannel.send(msg)
                        }
                    }
                }
            }
        }
    }
}