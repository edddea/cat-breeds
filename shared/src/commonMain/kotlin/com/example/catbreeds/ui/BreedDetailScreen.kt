package com.example.catbreeds.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.catbreeds.presentation.BreedDetailViewModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.core.component.KoinComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedDetailScreen(
    breedId: String,
    onBack: () -> Unit
) {
    val vm = rememberKoin<BreedDetailViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(breedId) { vm.load(breedId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.breed?.name ?: "Details") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                state.error != null -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.error ?: "Error", color = MaterialTheme.colorScheme.error)
                }

                state.breed != null -> {
                    val currentBreed = state.breed
                    Column(Modifier.fillMaxSize().padding(16.dp)) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(240.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!currentBreed?.imageUrl.isNullOrBlank()) {
                                KamelImage(
                                    resource = asyncPainterResource(currentBreed.imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text("🐱", style = MaterialTheme.typography.displayMedium)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "About",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { vm.onToggleFavorite() }) {
                                Text(
                                    if (currentBreed?.isFavorite == true) {
                                        "♥"
                                    } else {
                                        "♡"
                                    },
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }

                        Text(
                            currentBreed?.description ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(Modifier.height(12.dp))
                        MetaRow("Origin", currentBreed?.origin ?: "")
                        MetaRow("Temperament", currentBreed?.temperament ?: "")
                        MetaRow("Life span", currentBreed?.lifeSpan ?: "")
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            "$label:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.width(120.dp)
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private inline fun <reified T : Any> rememberKoin(): T {
    return remember {
        val koinComponent = object : KoinComponent {}
        koinComponent.getKoin().get<T>()
    }
}
