package com.example.catbreeds.data.repo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.catbreeds.data.platform.currentTimeMillis
import com.example.catbreeds.db.CatDatabase
import com.example.catbreeds.domain.model.Breed
import com.example.catbreeds.domain.repo.FavoritesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoritesRepositoryImpl(
    private val db: CatDatabase
) : FavoritesRepository {

    override fun favorites(): Flow<Result<List<Breed>>> =
        db.catDatabaseQueries.selectFavorites()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                Result.success(rows.map { it.toDomain(isFavorite = true) })
            }

    override suspend fun toggleFavorite(breedId: String): Result<Boolean> = runCatching {
        withContext(Dispatchers.Default) {
            val existsRow = db.catDatabaseQueries.isFavorite(breedId).executeAsOne()

            if (existsRow) {
                db.catDatabaseQueries.removeFavorite(breedId)
                false
            } else {
                db.catDatabaseQueries.addFavorite(breedId, currentTimeMillis())
                true
            }
        }
    }

    override fun isFavorite(breedId: String): Flow<Boolean> =
        db.catDatabaseQueries.isFavorite(breedId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { (it ?: 0L) == 1L }
}
