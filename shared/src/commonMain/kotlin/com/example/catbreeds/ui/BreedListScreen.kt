package com.example.catbreeds.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.catbreeds.domain.model.Breed
import com.example.catbreeds.presentation.BreedListViewModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.core.component.KoinComponent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BreedListScreen(
    onSelectBreed: (String) -> Unit
) {
    val vm = rememberKoin<BreedListViewModel>()
    val state by vm.state.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { vm.refresh() }
    )

    Box(Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
        Column(Modifier.fillMaxSize()) {
            if (state.isOfflineBanner) {
                AssistChip(
                    onClick = {},
                    label = { Text("Offline / showing cached data") },
                    modifier = Modifier.padding(12.dp)
                )
            }
            if (state.error != null) {
                Text(
                    "Note: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(state.items, key = { it.id }) { breed ->
                    BreedRow(breed = breed, onClick = { onSelectBreed(breed.id) })
                    Divider()
                }

                item {
                    if (state.canLoadMore) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator()
                            } else {
                                OutlinedButton(onClick = { vm.loadNextPage() }) {
                                    Text("Load more")
                                }
                            }
                        }
                    } else {
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun BreedRow(breed: Breed, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BreedAvatar(url = breed.imageUrl, isFavorite = breed.isFavorite)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    breed.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (breed.isFavorite) {
                    Text("♥", color = MaterialTheme.colorScheme.primary)
                }
            }
            Text(
                breed.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun BreedAvatar(url: String?, isFavorite: Boolean) {
    val shape = MaterialTheme.shapes.small
    Box(
        modifier = Modifier.size(56.dp).clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (!url.isNullOrBlank()) {
            KamelImage(
                resource = asyncPainterResource(url),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text("🐱")
        }
    }
}

@Composable
private inline fun <reified T : Any> rememberKoin(): T {
    return remember {
        val koinComponent = object : KoinComponent {}
        koinComponent.getKoin().get<T>()
    }
}
