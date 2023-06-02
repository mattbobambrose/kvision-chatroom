package com.github.mattbobambrose.chatroom

import io.kvision.annotations.KVService
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

@KVService
interface IWsService {
    suspend fun openBidirChannel(input: ReceiveChannel<UntimedMessage>, output: SendChannel<ChatMessage>) {
    }

    suspend fun openBidirChannel(handler: suspend (SendChannel<UntimedMessage>, ReceiveChannel<ChatMessage>) -> Unit) {
    }
}