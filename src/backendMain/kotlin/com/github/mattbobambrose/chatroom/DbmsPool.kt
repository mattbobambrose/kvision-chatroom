package com.github.mattbobambrose.chatroom

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import kotlin.time.Duration.Companion.minutes

object DbmsPool {
    val dbms by lazy {
        Database.connect(
            HikariDataSource(
                HikariConfig()
                    .apply {
                        driverClassName = "com.impossibl.postgres.jdbc.PGDriver"
                        jdbcUrl = "jdbc:pgsql://localhost:5432/postgres"
                        username = "postgres"
                        password = "docker"
                        maximumPoolSize = 10
                        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                        maxLifetime = 30.minutes.inWholeMilliseconds
                        validate()
                    }
            )
        )
    }
}