package com.example.catbreeds.presentation

import com.example.catbreeds.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> get() = _state

    fun onUsernameChange(v: String) {
        _state.value = _state.value.copy(username = v, error = null)
    }

    fun onPasswordChange(v: String) {
        _state.value = _state.value.copy(password = v, error = null)
    }

    fun submit() {
        val s = _state.value
        _state.value = s.copy(isLoading = true, error = null, isSuccess = false)

        if (s.username.isNotEmpty() && s.password.isNotEmpty()) {
            scope.launch {
                val res = loginUseCase(s.username, s.password)
                _state.value = if (res.isSuccess) {
                    _state.value.copy(isLoading = false, isSuccess = true)
                } else {
                    _state.value.copy(
                        isLoading = false,
                        error = res.exceptionOrNull()?.message ?: "Login failed"
                    )
                }
            }
        }
    }

    fun consumeSuccess() {
        _state.value = _state.value.copy(isSuccess = false)
    }
}
