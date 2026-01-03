package com.example.catbreeds.domain.repo

import com.example.catbreeds.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun sessionFlow(): Flow<Session?>
    suspend fun login(username: String, password: String): Result<Session>
    suspend fun logout()
}
