package com.example.catbreeds.domain.usecase

import com.example.catbreeds.domain.repo.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String) =
        authRepository.login(username, password)
}
