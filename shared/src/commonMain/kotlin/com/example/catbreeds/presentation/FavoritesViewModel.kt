package com.example.catbreeds.presentation

import com.example.catbreeds.domain.model.Breed
import com.example.catbreeds.domain.usecase.GetFavoritesUseCase
import com.example.catbreeds.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val items: List<Breed> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class FavoritesViewModel(
    private val getFavorites: GetFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    init {
        scope.launch {
            getFavorites().collect { res ->
                _state.value = if (res.isSuccess) {
                    FavoritesUiState(items = res.getOrNull().orEmpty(), isLoading = false)
                } else {
                    FavoritesUiState(items = emptyList(), isLoading = false, error = res.exceptionOrNull()?.message)
                }
            }
        }
    }

    fun remove(id: String) {
        scope.launch { toggleFavorite(id) }
    }
}
