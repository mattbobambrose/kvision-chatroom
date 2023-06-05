package com.github.mattbobambrose.chatroom

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object MessagesTable : IntIdTable(name = "chatroom.messages") {
    val username = text("username")
    val room = text("room")
    val message = text("message")
    val createdAt = datetime("created_at")
}