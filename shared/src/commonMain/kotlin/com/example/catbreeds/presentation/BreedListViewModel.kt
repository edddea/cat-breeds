package com.example.catbreeds.presentation

import com.example.catbreeds.domain.model.Breed
import com.example.catbreeds.domain.usecase.GetBreedsPageUseCase
import com.example.catbreeds.domain.usecase.RefreshBreedsPageUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BreedListUiState(
    val items: List<Breed> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val isOfflineBanner: Boolean = false,
    val canLoadMore: Boolean = true
)

class BreedListViewModel(
    private val getBreedsPage: GetBreedsPageUseCase,
    private val refreshPage: RefreshBreedsPageUseCase
) : BaseViewModel() {

    private val pageSize = 20
    private var page = 0
    private var collectingJob: Job? = null

    private val _state = MutableStateFlow(BreedListUiState(isLoading = true))
    val state: StateFlow<BreedListUiState> = _state.asStateFlow()

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        page = 0
        _state.value = BreedListUiState(isLoading = true)
        collectPage(page, replace = true)
    }

    fun loadNextPage() {
        val s = _state.value
        if (s.isLoading || s.isRefreshing || !s.canLoadMore) return
        page += 1
        _state.value = s.copy(isLoading = true, error = null)
        collectPage(page, replace = false)
    }

    fun refresh() {
        val s = _state.value
        if (s.isRefreshing) return
        _state.value = s.copy(isRefreshing = true, error = null, isOfflineBanner = false)
        scope.launch {
            val res = refreshPage(0, pageSize)
            _state.value = if (res.isSuccess) {
                _state.value.copy(isRefreshing = false)
            } else {
                _state.value.copy(
                    isRefreshing = false,
                    isOfflineBanner = true,
                    error = res.exceptionOrNull()?.message ?: "Failed to refresh"
                )
            }
            // re-collect first page to update items from cache
            loadFirstPage()
        }
    }

    private fun collectPage(page: Int, replace: Boolean) {
        collectingJob?.cancel()
        collectingJob = scope.launch {
            getBreedsPage(page, pageSize).collect { result ->
                if (result.isSuccess) {
                    val list = result.getOrNull().orEmpty()
                    val merged = if (replace) list else mergeById(_state.value.items, list)
                    _state.value = _state.value.copy(
                        items = merged,
                        isLoading = false,
                        error = null,
                        canLoadMore = list.size == pageSize,
                        isOfflineBanner = false
                    )
                } else {
                    // show cached list but mark offline
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isOfflineBanner = true,
                        error = result.exceptionOrNull()?.message ?: "Offline"
                    )
                }
            }
        }
    }

    private fun mergeById(existing: List<Breed>, incoming: List<Breed>): List<Breed> {
        val map = existing.associateBy { it.id }.toMutableMap()
        incoming.forEach { map[it.id] = it }
        return map.values.sortedBy { it.name }
    }
}
