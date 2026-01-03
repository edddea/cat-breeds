package com.example.catbreeds.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.catbreeds.presentation.SessionViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

sealed interface Screen {
    data object Login : Screen
    data object Breeds : Screen
    data class Detail(val id: String, val fromFavorites: Boolean = false) : Screen
    data object Favorites : Screen
}

@Composable
fun AppRoot() {
    val sessionVm = rememberKoin<SessionViewModel>()
    val session by sessionVm.session.collectAsState()

    var screen by remember { mutableStateOf<Screen>(Screen.Login) }

    LaunchedEffect(session) {
        screen = if (session == null) Screen.Login else Screen.Breeds
    }

    Surface(Modifier.fillMaxSize()) {
        when (val currentScreen = screen) {
            Screen.Login -> LoginScreen(
                onLoggedIn = { screen = Screen.Breeds }
            )

            Screen.Breeds -> MainScaffold(
                startTab = 0,
                onSelectBreed = { id -> screen = Screen.Detail(id) },
                onSelectFavorites = { screen = Screen.Favorites },
                onLogout = { sessionVm.logout() }
            )

            Screen.Favorites -> MainScaffold(
                startTab = 1,
                onSelectBreed = { id -> screen = Screen.Detail(id, fromFavorites = true) },
                onSelectFavorites = { /* already */ },
                onLogout = { sessionVm.logout() }
            )

            is Screen.Detail -> BreedDetailScreen(
                breedId = currentScreen.id,
                onBack = {
                    screen = if (currentScreen.fromFavorites) Screen.Favorites else Screen.Breeds
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(
    startTab: Int,
    onSelectBreed: (String) -> Unit,
    onSelectFavorites: () -> Unit,
    onLogout: suspend () -> Unit
) {
    var tab by remember { mutableIntStateOf(startTab) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cat Breeds") }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Breeds") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Favorites") })
                Spacer(Modifier.weight(1f))
                val cs = rememberCoroutineScope()
                var loggingOut by remember { mutableStateOf(false) }
                TextButton(
                    enabled = !loggingOut,
                    onClick = {
                        loggingOut = true
                        cs.launch {
                            onLogout()
                            loggingOut = false
                        }
                    }
                ) { Text(if (loggingOut) "…" else "Logout") }
            }

            when (tab) {
                0 -> BreedListScreen(onSelectBreed = onSelectBreed)
                1 -> FavoritesScreen(onSelectBreed = onSelectBreed)
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
