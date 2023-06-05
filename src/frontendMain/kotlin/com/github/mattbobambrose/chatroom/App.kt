package com.github.mattbobambrose.chatroom

import io.kvision.*
import io.kvision.core.onChange
import io.kvision.core.onEvent
import io.kvision.form.select.select
import io.kvision.form.text.text
import io.kvision.html.Span
import io.kvision.modal.Alert
import io.kvision.panel.root
import io.kvision.panel.vPanel
import io.kvision.rest.RestClient
import io.kvision.rest.call
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.utils.ENTER_KEY
import io.kvision.utils.px
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.await
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.js.Promise

val AppScope = CoroutineScope(window.asCoroutineDispatcher())

class App : Application() {
    val sendChannel = Channel<ChatMessage> { }
    val receiveChannel = Channel<ChatMessage> { }
    val restClient = RestClient()
    val connected = ObservableValue(false)

    override fun start(state: Map<String, Any>) {
        AppScope.launch {
            // Sets the browser id/cookie
            val resp: Promise<BrowserSessionResponse> = restClient.call("/${Constants.AssignBrowserId}")
            val id = resp.await().id
            println("BrowserSessionResponse: $id")

            WsModel.connectToWebSocket(sendChannel, receiveChannel)
            connected.value = true
        }

        root("kvapp") {
            vPanel {
                marginLeft = 50.px
                val usernameBox = text {
                    label = "Username"
                    placeholder = "What is your name?"
                }

                val roomChoice =
                    select {
                        label = "Room"
                        options = listOf(
                            "Room 1" to "Room 1",
                            "Room 2" to "Room 2",
                            "Room 3" to "Room 3"
                        )
                        bind(connected) {
                            disabled = !it
                        }
                    }

                val messageBox =
                    text {
                        placeholder = "What would you like to talk about?"
                        bind(connected) {
                            disabled = !it
                        }
                    }
                val chatHistory = vPanel {}

                roomChoice.onChange {
                    chatHistory.removeAll()
                    val room = roomChoice.value.orEmpty()
                    if (room.isNotBlank()) {
                        AppScope.launch {
                            Model.changeRoom(room)
                        }
                    }
                }

                messageBox.onEvent {
                    keydown = { it ->
                        if (it.keyCode == ENTER_KEY) {
                            val message = messageBox.value.orEmpty()
                            val room = roomChoice.value.orEmpty()
                            if (message.isBlank()) {
                                Alert.show("Please enter a message")
                            } else if (room.isBlank()) {
                                Alert.show("Please select a room")
                            } else {
                                val username =
                                    usernameBox.value.orEmpty().let {
                                        it.ifBlank {
                                            usernameBox.value = ""
                                            "Anonymous"
                                        }
                                    }
                                AppScope.launch {
                                    sendChannel.send(ChatMessage(username, room, message))
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