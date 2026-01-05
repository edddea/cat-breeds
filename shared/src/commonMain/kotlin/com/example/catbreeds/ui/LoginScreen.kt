package com.example.catbreeds.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.catbreeds.domain.definitions.Constants.CREDENTIALS_RULES
import com.example.catbreeds.domain.definitions.Constants.PASSWORD_LABEL
import com.example.catbreeds.domain.definitions.Constants.SIGN_IN_LABEL
import com.example.catbreeds.domain.definitions.Constants.SIGN_IN_PROGRESS_LABEL
import com.example.catbreeds.domain.definitions.Constants.UNKNOWN_ERROR
import com.example.catbreeds.domain.definitions.Constants.USERNAME_LABEL
import com.example.catbreeds.presentation.LoginViewModel
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit
) {
    val vm = rememberKoin<LoginViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            vm.consumeSuccess()
            onLoggedIn()
        }
    }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            delay(3000L)
            vm.onResetLoading()
        }
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = {
                vm.onUsernameChange(it)
            },
            label = { Text(USERNAME_LABEL) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = {
                vm.onPasswordChange(it)
            },
            label = { Text(PASSWORD_LABEL) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        if (state.error != null) {
            Text(state.error ?: UNKNOWN_ERROR, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = vm::submit,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (state.isLoading) {
                    SIGN_IN_PROGRESS_LABEL
                } else {
                    SIGN_IN_LABEL
                }
            )
        }

        Spacer(Modifier.height(12.dp))
        Text(
            CREDENTIALS_RULES,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private inline fun <reified T : Any> rememberKoin(): T {
    return remember {
        val koinComponent = object : KoinComponent {}
        koinComponent.getKoin().get<T>()
    }
}

