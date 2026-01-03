package com.example.catbreeds.domain.usecase

import com.example.catbreeds.domain.repo.FavoritesRepository

class ToggleFavoriteUseCase(
    private val repo: FavoritesRepository
) {
    suspend operator fun invoke(breedId: String) = repo.toggleFavorite(breedId)
}
