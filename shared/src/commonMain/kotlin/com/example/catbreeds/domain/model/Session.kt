package com.example.catbreeds.domain.model

data class Session(
    val token: String,
    val username: String,
    val expiresAtEpochMs: Long
)
