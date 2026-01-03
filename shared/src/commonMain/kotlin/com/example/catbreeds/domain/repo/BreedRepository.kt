package com.example.catbreeds.domain.repo

import com.example.catbreeds.domain.model.Breed
import kotlinx.coroutines.flow.Flow

interface BreedRepository {
    /**
     * Returns the cached page immediately (if any), then tries to refresh from network.
     */
    fun breedsPage(page: Int, pageSize: Int): Flow<Result<List<Breed>>>

    suspend fun refreshPage(page: Int, pageSize: Int): Result<Unit>

    fun breedById(id: String): Flow<Result<Breed>>
}
