package com.example.catbreeds.domain.usecase

import com.example.catbreeds.domain.repo.BreedRepository

class RefreshBreedsPageUseCase(
    private val repo: BreedRepository
) {
    suspend operator fun invoke(page: Int, pageSize: Int) = repo.refreshPage(page, pageSize)
}
