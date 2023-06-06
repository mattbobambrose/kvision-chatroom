package com.github.mattbobambrose.chatroom

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object MessagesTable : IntIdTable(name = "chatroom.messages") {
    val username = text("username")
    val roomRef = integer("room_ref")
    val message = text("message")
    val createdAt = datetime("created_at")
}

object RoomsTable : IntIdTable(name = "chatroom.rooms") {
    val name = text("name")
}