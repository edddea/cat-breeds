package com.example.catbreeds.presentation

import com.example.catbreeds.domain.repo.AuthRepository
import com.example.catbreeds.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SessionViewModel(
    authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel() {

    val session = authRepository.sessionFlow()
        .stateIn(scope, SharingStarted.Eagerly, null)

    suspend fun logout() {
        logoutUseCase()
    }
}
