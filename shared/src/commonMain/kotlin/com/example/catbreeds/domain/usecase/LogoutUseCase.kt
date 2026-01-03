package com.example.catbreeds.domain.usecase

import com.example.catbreeds.domain.repo.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logout()
}
