package com.example.catbreeds.data.repo

import com.example.catbreeds.data.platform.currentTimeMillis
import com.example.catbreeds.data.platform.randomToken
import com.example.catbreeds.db.CatDatabase
import com.example.catbreeds.domain.model.Session
import com.example.catbreeds.domain.repo.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val db: CatDatabase
) : AuthRepository {

    override fun sessionFlow(): Flow<Session?> =
        db.catDatabaseQueries.getSession()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { row ->
                row?.let { Session(it.token, it.username, it.expires_at) }
            }

    override suspend fun login(username: String, password: String): Result<Session> {
        val validated = validate(username, password) ?: return Result.failure(
            IllegalArgumentException("Invalid username or password")
        )

        val session = Session(
            token = randomToken(),
            username = validated.first,
            expiresAtEpochMs = currentTimeMillis() + 1000L * 60L * 60L * 24L // 24h
        )

        return runCatching {
            withContext(Dispatchers.Default) {
                db.catDatabaseQueries.saveSession(
                    session.token,
                    session.username,
                    session.expiresAtEpochMs
                )
            }
            session
        }
    }

    override suspend fun logout() {
        withContext(Dispatchers.Default) {
            db.catDatabaseQueries.clearSession()
        }
    }

    private fun validate(username: String, password: String): Pair<String, String>? {
        val u = username.trim()
        val p = password

        val userOk = u.length >= 4 && u.matches(Regex("^[a-zA-Z0-9._-]+$"))
        val passOk =
            p.length >= 6 && !p.contains(" ") && p.any { it.isDigit() } && p.any { it.isLetter() }

        return if (userOk && passOk) u to p else null
    }
}
