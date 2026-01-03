package com.example.catbreeds.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.catbreeds.presentation.FavoritesViewModel
import org.koin.core.component.KoinComponent

@Composable
fun FavoritesScreen(
    onSelectBreed: (String) -> Unit
) {
    val vm = rememberKoin<FavoritesViewModel>()
    val state by vm.state.collectAsState()

    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        state.items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No favorites yet. Tap ♥ on a breed.")
        }
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(state.items, key = { it.id }) { breed ->
                ListItem(
                    headlineContent = { Text(breed.name) },
                    supportingContent = { Text(breed.shortDescription, maxLines = 2) },
                    trailingContent = {
                        IconButton(onClick = { vm.remove(breed.id) }) {
                            Text("♥")
                        }
                    },
                    modifier = Modifier.clickable { onSelectBreed(breed.id) }
                )
                Divider()
            }
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
