package com.example.catbreeds.presentation

import com.example.catbreeds.domain.model.Breed
import com.example.catbreeds.domain.usecase.GetBreedDetailUseCase
import com.example.catbreeds.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BreedDetailUiState(
    val breed: Breed? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class BreedDetailViewModel(
    private val getDetail: GetBreedDetailUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(BreedDetailUiState())
    val state: StateFlow<BreedDetailUiState> = _state.asStateFlow()

    fun load(id: String) {
        _state.value = BreedDetailUiState(isLoading = true)
        scope.launch {
            getDetail(id).collect { res ->
                _state.value = if (res.isSuccess) {
                    BreedDetailUiState(breed = res.getOrNull(), isLoading = false)
                } else {
                    BreedDetailUiState(
                        breed = null,
                        isLoading = false,
                        error = res.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    fun onToggleFavorite() {
        val id = _state.value.breed?.id ?: return
        scope.launch { toggleFavorite(id) }
    }
}
