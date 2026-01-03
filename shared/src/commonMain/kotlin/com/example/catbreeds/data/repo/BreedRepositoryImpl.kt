package com.example.catbreeds.data.repo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.catbreeds.data.platform.currentTimeMillis
import com.example.catbreeds.data.remote.TheCatApi
import com.example.catbreeds.db.CatDatabase
import com.example.catbreeds.domain.model.Breed
import com.example.catbreeds.domain.repo.BreedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class BreedRepositoryImpl(
    private val api: TheCatApi,
    private val db: CatDatabase,
) : BreedRepository {

    override fun breedsPage(page: Int, pageSize: Int): Flow<Result<List<Breed>>> {
        val offset = page * pageSize

        val cachedFlow = db.catDatabaseQueries
            .selectPage(pageSize.toLong(), offset.toLong())
            .asFlow()
            .mapToList(Dispatchers.Default)

        val favIdsFlow = db.catDatabaseQueries
            .selectFavoriteIds()
            .asFlow()
            .mapToList(Dispatchers.Default)

        val listFlow: Flow<List<Breed>> = combine(cachedFlow, favIdsFlow) { cachedRows, favIds ->
            val favSet = favIds.toSet()
            cachedRows.map { it.toDomain(isFavorite = favSet.contains(it.id)) }
        }

        return flow {
            emit(Result.success(listFlow.first()))
            val refresh = refreshPage(page, pageSize)
            if (refresh.isFailure) emit(Result.failure(refresh.exceptionOrNull()!!))
            else emit(Result.success(listFlow.first()))
        }
    }

    override suspend fun refreshPage(page: Int, pageSize: Int): Result<Unit> = runCatching {
        val dtos = api.getBreeds(page = page, limit = pageSize)
        withContext(Dispatchers.Default) {
            val now = currentTimeMillis()
            dtos.forEach { dto ->
                db.catDatabaseQueries.upsertBreed(
                    id = dto.id,
                    name = dto.name,
                    description = dto.description.ifBlank { "(No description)" },
                    origin = dto.origin,
                    temperament = dto.temperament,
                    life_span = dto.life_span,
                    image_url = dto.image?.url,
                    updated_at = now
                )
            }
        }
    }

    override fun breedById(id: String): Flow<Result<Breed>> {
        val breedFlow = db.catDatabaseQueries
            .selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)

        val favFlow = db.catDatabaseQueries
            .isFavorite(id)
            .asFlow()
            .mapToOne(Dispatchers.Default)

        return breedFlow.combine(favFlow) { row, favResult ->
            if (row == null) {
                Result.failure(NoSuchElementException("Breed not found in cache"))
            } else {
                Result.success(row.toDomain(isFavorite = favResult))
            }
        }
    }
}

