package com.example.catbreeds.domain.usecase

import com.example.catbreeds.domain.repo.BreedRepository

class GetBreedDetailUseCase(
    private val repo: BreedRepository
) {
    operator fun invoke(id: String) = repo.breedById(id)
}
