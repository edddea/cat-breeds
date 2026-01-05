package com.example.catbreeds.domain.repo

import com.example.catbreeds.domain.model.Breed
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun favorites(): Flow<Result<List<Breed>>>
    suspend fun toggleFavorite(breedId: String): Result<Boolean>
    fun isFavorite(breedId: String): Flow<Boolean>
}
