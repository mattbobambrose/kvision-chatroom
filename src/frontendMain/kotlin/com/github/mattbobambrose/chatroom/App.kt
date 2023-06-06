package com.github.mattbobambrose.chatroom

import io.kvision.*
import io.kvision.core.onChange
import io.kvision.core.onEvent
import io.kvision.form.select.SelectInput
import io.kvision.form.text.Text
import io.kvision.html.Span
import io.kvision.modal.Alert
import io.kvision.panel.VPanel
import io.kvision.panel.root
import io.kvision.panel.vPanel
import io.kvision.rest.RestClient
import io.kvision.rest.call
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.observableListOf
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
    val roomList = observableListOf<RoomIdentifier>()

    val usernameBox = Text {
        label = "Username"
        placeholder = "What is your name?"
    }

    val roomChoice = SelectInput(selectSize = 1) {
        onChange {
            chatHistory.removeAll()
            val roomId = value?.toInt()
//                println("RoomId: $roomId")
            if (roomId != null) {
                AppScope.launch {
                    Model.changeRoom(roomId)
                }
            }
        }

        bind(roomList) {
            options = it.map { roomId ->
                roomId.id.toString() to roomId.room
            }
            selectedIndex = 0
        }

        bind(connected) {
            disabled = !it
            if (connected.value) {
//                    println("Connected")
//                    println("Selected: $value")
                val roomId = value?.toInt()
                if (roomId != null) {
                    AppScope.launch {
                        Model.changeRoom(roomId)
                    }
                }
            }
        }
    }

    val messageBox = Text {
        placeholder = "What would you like to talk about?"
        bind(connected) {
            disabled = !it
        }

        onEvent {
            keydown = { it ->
                if (it.keyCode == ENTER_KEY) {
                    val message = value.orEmpty()
                    val roomId = roomChoice.value?.toInt()
                    if (message.isBlank()) {
                        Alert.show("Please enter a message")
                    } else if (roomId == null) {
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
//                                println("Sending message: $message from $username in room $roomId")
                            sendChannel.send(ChatMessage(username, roomId, message))
                        }
                        value = ""
                    }
                }
            }
        }
    }

    val chatHistory = VPanel {}

    override fun start(state: Map<String, Any>) {
        AppScope.launch {
            for (message in receiveChannel) {
                chatHistory.add(Span(message.displayMessage()))
            }
        }

        AppScope.launch {
            // Sets the browser id/cookie
            val resp: Promise<BrowserSessionResponse> = restClient.call("/${Constants.AssignBrowserId}")
            val id = resp.await().id
//            println("BrowserSessionResponse: $id")

            // Connect to the websocket
            WsModel.connectToWebSocket(sendChannel, receiveChannel)

            // Fetch the list of rooms
            roomList.addAll(Model.getRoomNames())

            // Indicate the client is connected
            connected.value = true
        }


        root("kvapp") {
            vPanel {
                marginLeft = 50.px

                add(usernameBox)
                add(roomChoice)
                add(messageBox)
                add(chatHistory)
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