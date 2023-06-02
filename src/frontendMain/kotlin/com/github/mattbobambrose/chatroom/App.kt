package com.github.mattbobambrose.chatroom

import io.kvision.*
import io.kvision.core.onEvent
import io.kvision.form.text.text
import io.kvision.html.Span
import io.kvision.modal.Alert
import io.kvision.panel.root
import io.kvision.panel.vPanel
import io.kvision.utils.ENTER_KEY
import io.kvision.utils.px
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

val AppScope = CoroutineScope(window.asCoroutineDispatcher())

class App : Application() {
    val sendChannel = Channel<UntimedMessage> { }
    val receiveChannel = Channel<ChatMessage> { }
    override fun start(state: Map<String, Any>) {
        WsModel.connectToWebSocket(sendChannel, receiveChannel)
        root("kvapp") {
            vPanel {
                marginLeft = 50.px
                val usernameBox = text {
                    label = "Username"
                    placeholder = "What is your name?"
                }
                val messageBox = text { placeholder = "What would you like to talk about?" }
                val chatHistory = vPanel {}

                messageBox.onEvent {
                    keydown = { it ->
                        if (it.keyCode == ENTER_KEY) {
                            val message = messageBox.value.orEmpty()
                            if (message.isBlank()) {
                                Alert.show("Please enter a message")
                            } else {
                                val username =
                                    usernameBox.value.orEmpty().let {
                                        it.ifBlank {
                                            usernameBox.value = ""
                                            "Anonymous"
                                        }
                                    }
                                AppScope.launch {
                                    sendChannel.send(UntimedMessage(message, username))
                                }
                                messageBox.value = ""
                            }
                        }
                    }
                }

                AppScope.launch {
                    for (message in receiveChannel) {
                        chatHistory.add(Span(message.displayMessage()))
                    }
                }

                /*
                                AppScope.launch {
                                    var lastTimeChecked: Instant = Instant.DISTANT_PAST
                                    while (true) {
                                        runCatching {
                                            val timedList = Model.getMessages(lastTimeChecked)
                                            lastTimeChecked = timedList.lastTimeChecked
                                            timedList.messages.forEach { chatMessage ->
                                                chatHistory.add(Span(chatMessage.displayMessage()))
                                            }
                                        }.onFailure { e ->
                                            println(e)
                                        }
                                        delay(1.seconds)
                                    }
                                }
                */
            }
        }
    }
}

fun main() {
    startApplication(
        ::App,
        module.hot,
        BootstrapModule,
        BootstrapCssModule,
        FontAwesomeModule,
        CoreModule
    )
}