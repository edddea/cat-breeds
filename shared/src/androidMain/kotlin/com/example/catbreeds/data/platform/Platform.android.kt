package com.example.catbreeds.data.platform

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.catbreeds.db.CatDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.UUID

private lateinit var appContext: Context

fun initPlatform(context: Context) {
    appContext = context.applicationContext
}

actual fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

actual fun createSqlDriver(): SqlDriver =
    AndroidSqliteDriver(CatDatabase.Schema, appContext, "catbreeds.db")

actual fun currentTimeMillis(): Long = System.currentTimeMillis()
actual fun randomToken(): String = UUID.randomUUID().toString()
