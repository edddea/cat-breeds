package com.example.catbreeds.data.platform

import app.cash.sqldelight.db.SqlDriver
import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient
expect fun createSqlDriver(): SqlDriver
expect fun currentTimeMillis(): Long
expect fun randomToken(): String
