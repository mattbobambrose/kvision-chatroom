package com.github.pambrose.kvision_websockets

import com.github.mattbobambrose.chatroom.BrowserSessionResponse
import com.github.mattbobambrose.chatroom.Constants.BROWSER_ID_COOKIE
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import java.security.SecureRandom
import kotlin.time.Duration.Companion.days

typealias PipelineCall = PipelineContext<*, ApplicationCall>

data class BrowserSession(val id: String) {
    override fun toString(): String = id
}

fun randomId(length: Int = 10, charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')) =
    SecureRandom().let { random ->
        (1..length)
            .map { random.nextInt(charPool.size) }
            .map { charPool[it] }
            .joinToString("")
    }

fun SessionsConfig.configureSessionIdCookie() {
    cookie<BrowserSession>(BROWSER_ID_COOKIE) {
        cookie.path = "/"
        cookie.extensions["SameSite"] = "strict"
        cookie.maxAgeInSeconds = 3650.days.inWholeSeconds
        cookie.httpOnly = true
        cookie.secure = false // TODO This is potentially a problem
    }
}

fun PipelineCall.assignBrowserSession(): BrowserSessionResponse {
    val currSession = call.sessions.get<BrowserSession>()
    return if (currSession != null) {
        println("Found browser session: $currSession - ${call.request.origin.remoteHost}")
        BrowserSessionResponse(currSession.id, "BrowserSessionId already assigned")
    } else {
        val browserSession = BrowserSession(id = randomId(15))
        call.sessions.set(browserSession)
        println("Created browser session: $browserSession - ${call.request.origin.remoteHost}")
        BrowserSessionResponse(browserSession.id, "BrowserSessionId assigned")
    }
}