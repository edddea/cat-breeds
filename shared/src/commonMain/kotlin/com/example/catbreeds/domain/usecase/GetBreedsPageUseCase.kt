package com.example.catbreeds.domain.usecase

import com.example.catbreeds.domain.repo.BreedRepository

class GetBreedsPageUseCase(
    private val repo: BreedRepository
) {
    operator fun invoke(page: Int, pageSize: Int) = repo.breedsPage(page, pageSize)
}
