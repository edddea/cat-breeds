package com.example.catbreeds.domain.usecase

import com.example.catbreeds.domain.repo.FavoritesRepository

class GetFavoritesUseCase(
    private val repo: FavoritesRepository
) {
    operator fun invoke() = repo.favorites()
}
