package com.example.catbreeds.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.catbreeds.di.sharedModules
import com.example.catbreeds.ui.AppRoot
import org.koin.core.context.startKoin

fun main() = application {
    startKoin { modules(sharedModules()) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "CatBreeds"
    ) {
        MaterialTheme {
            AppRoot()
        }
    }
}
