package com.example.cse3310app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cse3310app.ui.viewmodel.AuthViewModel

@Composable
fun SplashScreen(authViewModel: AuthViewModel, onNavigate: (Boolean) -> Unit) {
    val state by authViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.isLoggedIn, state.currentUserId) {
        onNavigate(state.isLoggedIn && state.currentUserId != null)
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text("Preparing local bookstore...")
    }
}

@Composable
fun LoginScreen(authViewModel: AuthViewModel, onRegisterClick: () -> Unit) {
    val state by authViewModel.uiState.collectAsStateWithLifecycle()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = { authViewModel.login(username, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }
        Button(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
            Text("Create account")
        }
        if (state.error != null) Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun RegisterScreen(onContinue: (String, String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = { onContinue(username, email, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Continue")
        }
    }
}

@Composable
fun GenrePreferenceScreen(
    authViewModel: AuthViewModel,
    username: String,
    email: String,
    password: String,
    onRegistered: () -> Unit
) {
    val selected = remember { mutableStateListOf<String>() }
    val genres = listOf("Fiction", "Science", "History", "Math", "Fantasy", "Programming")
    val state by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onRegistered()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Pick preferred genres")
        genres.forEach { genre ->
            Button(
                onClick = {
                    if (selected.contains(genre)) selected.remove(genre) else selected.add(genre)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selected.contains(genre)) "Selected: $genre" else genre)
            }
        }
        Button(onClick = {
            authViewModel.register(username, email, password, selected)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Finish registration")
        }
        if (state.error != null) Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
    }
}
