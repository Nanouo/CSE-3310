package com.example.cse3310app.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cse3310app.ui.viewmodel.AccountViewModel
import com.example.cse3310app.ui.viewmodel.AuthViewModel

@Composable
fun AccountScreen(userId: Long, accountViewModel: AccountViewModel, authViewModel: AuthViewModel) {
    val user by accountViewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(userId) { accountViewModel.load(userId) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Account", style = MaterialTheme.typography.headlineSmall)
        Text("Username: ${user?.username ?: "-"}")
        Text("Email: ${user?.email ?: "-"}")
        Text("Role: ${user?.role ?: "-"}")
        Text("Preferred Genres: ${user?.preferredGenres ?: "None"}")
        Button(onClick = { authViewModel.logout() }, modifier = Modifier.fillMaxWidth()) {
            Text("Logout")
        }
    }
}
