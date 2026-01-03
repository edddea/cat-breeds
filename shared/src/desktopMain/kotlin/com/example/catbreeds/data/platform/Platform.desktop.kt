package com.example.catbreeds.data.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.catbreeds.db.CatDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

actual fun createHttpClient(): HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

actual fun createSqlDriver(): SqlDriver {
    val dbFile = File(System.getProperty("user.home"), ".catbreeds/catbreeds.db")
    dbFile.parentFile?.mkdirs()
    val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
    try { CatDatabase.Schema.create(driver) } catch (_: Throwable) { /* already created */ }
    return driver
}

actual fun currentTimeMillis(): Long = System.currentTimeMillis()
actual fun randomToken(): String = UUID.randomUUID().toString()
