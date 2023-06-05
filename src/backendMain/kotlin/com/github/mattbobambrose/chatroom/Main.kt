package com.github.mattbobambrose.chatroom

import com.github.pambrose.kvision_websockets.assignBrowserSession
import com.github.pambrose.kvision_websockets.configureSessionIdCookie
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.kvision.remote.applyRoutes
import io.kvision.remote.getAllServiceManagers
import io.kvision.remote.kvisionInit

fun Application.main() {
    install(Compression)
    install(WebSockets)
    install(Sessions) {
        configureSessionIdCookie()
    }
    routing {
        getAllServiceManagers().forEach { applyRoutes(it) }
        get(Constants.AssignBrowserId) {
            val response = assignBrowserSession()
            call.respond(response)
        }
    }
    DbmsPool.dbms

    kvisionInit()
}